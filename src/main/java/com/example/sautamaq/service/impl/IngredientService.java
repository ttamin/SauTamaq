package com.example.sautamaq.service.impl;

import com.example.sautamaq.dto.IngredientDto;
import com.example.sautamaq.model.Ingredient;

import java.util.List;

public interface IngredientService {
    List<Ingredient> getIngredientsByIds(List<Long> collect);

    }
