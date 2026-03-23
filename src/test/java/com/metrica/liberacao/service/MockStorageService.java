package com.metrica.liberacao.service;

import com.metrica.liberacao.repository.StorageService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service("supabaseStorageService")
@Profile("!prod")
public class MockStorageService implements StorageService {

    @Override
    public String upload(String bucket, String path, MultipartFile file) {
        return path;
    }

    @Override
    public InputStream download(String bucket, String path) {
        return new ByteArrayInputStream(new byte[0]);
    }
}