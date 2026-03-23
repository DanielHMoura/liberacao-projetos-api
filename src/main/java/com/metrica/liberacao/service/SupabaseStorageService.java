package com.metrica.liberacao.service;

import com.metrica.liberacao.repository.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service("supabaseStorageService")
@Profile("prod")
public class SupabaseStorageService implements StorageService {

    @Value("${SUPABASE_URL}")
    private String supabaseUrl;

    @Value("${SUPABASE_SECRET_KEY}")
    private String supabaseServiceKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String upload(String bucket, String path, MultipartFile file) {
        String url = supabaseUrl + "/storage/v1/object/" + bucket + "/" + path;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(supabaseServiceKey);
        headers.setContentType(MediaType.APPLICATION_PDF);

        try {
            HttpEntity<byte[]> request =
                    new HttpEntity<>(file.getBytes(), headers);

            restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    request,
                    Void.class
            );

            // O path é definido por você
            return path;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar arquivo para o Supabase", e);
        }
    }

    @Override
    public InputStream download(String bucket, String path) {
        String url = supabaseUrl + "/storage/v1/object/" + bucket + "/" + path;

        try {
            URL parsedUrl = URI.create(url).toURL();
            HttpURLConnection connection = (HttpURLConnection) parsedUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + supabaseServiceKey);
            connection.connect();

            int status = connection.getResponseCode();
            if (status >= 400) {
                InputStream errorStream = connection.getErrorStream();
                String detalhes = errorStream == null
                        ? "sem detalhes"
                        : new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                connection.disconnect();
                throw new RuntimeException("Erro ao fazer download no Supabase. HTTP " + status + ": " + detalhes);
            }

            InputStream inputStream = connection.getInputStream();
            return new FilterInputStream(inputStream) {
                @Override
                public void close() throws IOException {
                    try {
                        super.close();
                    } finally {
                        connection.disconnect();
                    }
                }
            };
        } catch (IOException e) {
            throw new RuntimeException("Erro ao fazer download no Supabase", e);
        }
    }
}
