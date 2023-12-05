package com.example.sautamaq.service;

import com.example.sautamaq.model.Ingredient;
import com.example.sautamaq.repository.IngredientRepository;
import com.example.sautamaq.service.impl.IngredientService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class IngredientServiceImpl implements IngredientService {
    private final IngredientRepository ingredientRepository;

    public IngredientServiceImpl(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }


    @Override
    public List<Ingredient> getIngredientsByIds(List<Long> ingredientIds) {
        return ingredientRepository.findAllById(ingredientIds);
    }

}
