package com.example.sautamaq.service.impl;

import com.example.sautamaq.dto.RecipeDto;
import com.example.sautamaq.model.Recipe;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface RecipeService {
    Recipe createRecipe(RecipeDto recipeDto);
    void uploadRecipeImage(Long recipeId, String imagePath, byte[] imageBytes);
    RecipeDto getRecipeById(Long id);
    CompletableFuture<Void> uploadRecipeImageAsync(Long recipeId, MultipartFile file);

}
