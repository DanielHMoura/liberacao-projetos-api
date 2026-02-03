package com.metrica.liberacao.service;

import com.metrica.liberacao.repository.StorageService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("supabaseStorageService")
@Profile("test")
public class MockStorageService implements StorageService {

    @Override
    public String upload(String bucket, String path, MultipartFile file) {
        return path;
    }

    @Override
    public byte[] download(String bucket, String path) {
        return new byte[0];
    }
}