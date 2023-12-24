package com.example.sautamaq.service.impl;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String uploadRecipeImage(MultipartFile file, Long recipeId);
    String uploadCategoryImage(MultipartFile file, Long categoryId);
}
