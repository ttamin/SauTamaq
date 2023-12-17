package com.example.sautamaq.service;

import com.example.sautamaq.exception.NotFoundException;
import com.example.sautamaq.model.Ingredient;
import com.example.sautamaq.repository.IngredientRepository;
import com.example.sautamaq.service.impl.IngredientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class IngredientServiceImpl implements IngredientService {
    private final IngredientRepository ingredientRepository;

    public IngredientServiceImpl(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }



    @Override
    public Ingredient getIngredientById(Long id) {
        Optional<Ingredient> ingredientOptional = ingredientRepository.findById(id);

        return ingredientOptional.orElse(null);
    }


}
