package com.example.sautamaq.service.impl;

import com.example.sautamaq.dto.RecipeDto;
import com.example.sautamaq.model.Category;
import com.example.sautamaq.model.Ingredient;
import com.example.sautamaq.model.Instruction;
import com.example.sautamaq.model.Recipe;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface RecipeService {
    Recipe createRecipe(RecipeDto recipeDto);

    RecipeDto getRecipeById(Long id);
    CompletableFuture<Void> uploadRecipeImageAsync(Long recipeId, MultipartFile file);

    void deleteRecipe(Long recipeId);

    Recipe updateRecipeWithoutImage(Long recipeId, RecipeDto updatedRecipeDto);
    RecipeDto convertRecipeToDto(Recipe recipe);
}
