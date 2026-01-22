package com.metrica.liberacao.repository;

import org.springframework.web.multipart.MultipartFile;


public interface StorageService {
    String upload(String bucket, String patch, MultipartFile file);

    byte[] download(String bucket, String path);
}
