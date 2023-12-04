package com.example.sautamaq.service.impl;

import com.example.sautamaq.dto.RecipeDto;
import com.example.sautamaq.model.Recipe;
import com.example.sautamaq.model.RecipeIngredient;

import java.util.List;

public interface RecipeService {
    Recipe createRecipe(RecipeDto recipeDto);

    void updateRecipe(Long id, RecipeDto updatedRecipeDto);

    List<RecipeIngredient> getIngredientsByIds(List<Long> ingredientIds);

    Recipe getRecipeById(Long id);

    void deleteRecipe(Long id);
}
