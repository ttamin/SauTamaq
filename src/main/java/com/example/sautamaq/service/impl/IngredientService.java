package com.example.sautamaq.service.impl;

import com.example.sautamaq.model.Ingredient;
import java.util.List;

public interface IngredientService {
    Ingredient addIngredient(Ingredient ingredient);
    List<Ingredient> getAllIngredients();


}
