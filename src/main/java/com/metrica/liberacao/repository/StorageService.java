package com.metrica.liberacao.repository;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;


public interface StorageService {
    String upload(String bucket, String patch, MultipartFile file);

    InputStream download(String bucket, String path);
}
