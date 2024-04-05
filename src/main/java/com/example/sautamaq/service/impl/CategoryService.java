package com.example.sautamaq.service.impl;

import com.example.sautamaq.dto.CategoryDto;
import com.example.sautamaq.model.Category;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CategoryService {
        List<Category> getAllCategory();
        Category addCategory(CategoryDto categoryDto);
        void removeCategoryById(long id);

        void updateCategory(long id, Category updatedCategory);
        Category getCategoryById(long id);
        void uploadRecipeImage(Long recipeId, String imagePath, byte[] imageBytes);
        CompletableFuture<Void> uploadRecipeImageAsync(Long recipeId, MultipartFile file);

}
