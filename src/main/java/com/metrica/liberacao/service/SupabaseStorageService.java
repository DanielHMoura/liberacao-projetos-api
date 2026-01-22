package com.metrica.liberacao.service;

import com.metrica.liberacao.repository.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
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
    public byte[] download(String bucket, String path) {
        String url = supabaseUrl + "/storage/v1/object/" + bucket + "/" + path;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(supabaseServiceKey);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                byte[].class
        );

        return response.getBody();
    }
}
