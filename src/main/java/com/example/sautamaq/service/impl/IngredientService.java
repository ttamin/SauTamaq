package com.example.sautamaq.service.impl;

import com.example.sautamaq.dto.IngredientDto;
import com.example.sautamaq.dto.RecipeIngredientDto;
import com.example.sautamaq.model.Ingredient;
import com.example.sautamaq.model.RecipeIngredient;

import java.util.List;

public interface IngredientService {
    List<RecipeIngredient> getIngredientsByIds(List<Long> collect);
    RecipeIngredient convertDtoToEntity(RecipeIngredientDto ingredientDto);
    Ingredient convertDtoToEntity(IngredientDto ingredientDto);



    }
