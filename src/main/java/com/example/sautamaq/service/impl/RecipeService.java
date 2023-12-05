package com.example.sautamaq.service.impl;

import com.example.sautamaq.dto.RecipeDto;
import com.example.sautamaq.model.Recipe;

import java.util.List;

public interface RecipeService {
    Recipe createRecipe(RecipeDto recipeDto);
    void updateRecipeImage(Long recipeId, String imagePath);


}
