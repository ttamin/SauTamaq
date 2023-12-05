package com.example.sautamaq.service;

import com.example.sautamaq.dto.IngredientDto;
import com.example.sautamaq.dto.RecipeDto;
import com.example.sautamaq.exception.RecipeNotFoundException;
import com.example.sautamaq.model.Category;
import com.example.sautamaq.model.Ingredient;
import com.example.sautamaq.model.Recipe;
import com.example.sautamaq.repository.IngredientRepository;
import com.example.sautamaq.repository.RecipeRepository;
import com.example.sautamaq.service.impl.CategoryService;
import com.example.sautamaq.service.impl.IngredientService;
import com.example.sautamaq.service.impl.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    private final IngredientService ingredientService;
    private final CategoryService categoryService;

    @Autowired
    public RecipeServiceImpl(RecipeRepository recipeRepository, IngredientRepository ingredientRepository, IngredientService ingredientService, CategoryService categoryService) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.ingredientService = ingredientService;
        this.categoryService = categoryService;
    }

    @Override
    public Recipe createRecipe(RecipeDto recipeDto) {

        Category category = validateAndSetCategory(recipeDto.getCategory());

        Recipe recipe = new Recipe();
        recipe.setName(recipeDto.getName());
        recipe.setCategory(category);
        recipe.setImageData(recipeDto.getImageData());
        recipe.setCookingTime(recipeDto.getCookingTime());

        List<Ingredient> recipeIngredients = new ArrayList<>();

        for (IngredientDto ingredientDto : recipeDto.getIngredients()) {
            Ingredient recipeIngredient = new Ingredient();
            recipeIngredient.setName(ingredientDto.getName());
            recipeIngredient.setQuantity(ingredientDto.getQuantity());
            recipeIngredients.add(recipeIngredient);
        }

        recipe.setRecipeIngredients(recipeIngredients);
        return recipeRepository.save(recipe);

    }
//    private Ingredient validateAndCreateIngredient(String ingredientName) {
//        Optional<Ingredient> existingIngredient = ingredientRepository.findByName(ingredientName);
//
//        if (existingIngredient.isPresent()) {
//            return existingIngredient.get();
//        } else {
//            Ingredient newIngredient = new Ingredient();
//            newIngredient.setName(ingredientName);
//            return ingredientRepository.save(newIngredient);
//        }
//    }
private Category validateAndSetCategory(Category category) {
    if (category == null) {
        throw new IllegalArgumentException("Category cannot be null");
    }


    return category;
}
}