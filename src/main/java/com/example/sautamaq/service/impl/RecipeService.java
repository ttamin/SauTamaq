package com.example.sautamaq.service.impl;

import com.example.sautamaq.dto.RecipeDto;
import com.example.sautamaq.model.Recipe;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface RecipeService {
    List<Recipe> getAllRecipes();
    Optional<Recipe> getRecipeById(Long id);
    Recipe createRecipe(RecipeDto recipeDto, MultipartFile imageFile);
    void updateRecipeImage(Long recipeId, MultipartFile file);
    // TODO: updateRecipe, deleteRecipe



}
