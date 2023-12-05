package com.example.sautamaq.service.impl;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String uploadImage(MultipartFile file, Long recipeId);
}
