package com.example.sautamaq.service;

import com.example.sautamaq.dto.IngredientDto;
import com.example.sautamaq.dto.RecipeIngredientDto;
import com.example.sautamaq.model.Ingredient;
import com.example.sautamaq.model.RecipeIngredient;
import com.example.sautamaq.repository.IngredientRepository;
import com.example.sautamaq.repository.RecipeIngredientRepository;
import com.example.sautamaq.service.impl.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class IngredientServiceImpl implements IngredientService {
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final IngredientRepository ingredientRepository;

    @Autowired
    public IngredientServiceImpl(RecipeIngredientRepository recipeIngredientRepository, IngredientRepository ingredientRepository) {
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.ingredientRepository = ingredientRepository;
    }
    @Override
    public List<RecipeIngredient> getIngredientsByIds(List<Long> ingredientIds) {
        return recipeIngredientRepository.findAllById(ingredientIds);
    }
    @Override
    public RecipeIngredient convertDtoToEntity(RecipeIngredientDto ingredientDto) {
        Ingredient ingredient = ingredientRepository.findById(ingredientDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Ingredient with ID " + ingredientDto.getId() + " not found"));
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        recipeIngredient.setIngredient(ingredient);
        recipeIngredient.setQuantity(ingredientDto.getQuantity());

        return recipeIngredient;
    }

    @Override
    public Ingredient convertDtoToEntity(IngredientDto ingredientDto) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(ingredientDto.getName());
        return ingredient;
    }
}
