package com.metrica.liberacao.service;

import com.metrica.liberacao.repository.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@Profile({"test", "default"})
public class StorageServiceImpl implements StorageService {

    private final S3Client s3Client;

    @Autowired
    public StorageServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String upload(String bucket, String path, MultipartFile file) {
        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(path)
                    .build();
            s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));
            return path;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao fazer upload", e);
        }
    }

    @Override
    public InputStream download(String bucket, String path) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(path)
                .build();
        return s3Client.getObject(getRequest);
    }

}
