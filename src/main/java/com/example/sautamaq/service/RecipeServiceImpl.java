package com.example.sautamaq.service;

import com.example.sautamaq.dto.RecipeDto;
import com.example.sautamaq.dto.RecipeIngredientDto;
import com.example.sautamaq.exception.RecipeNotFoundException;
import com.example.sautamaq.model.Category;
import com.example.sautamaq.model.RecipeIngredient;
import com.example.sautamaq.model.Recipe;
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
    private final IngredientService ingredientService;
    private final CategoryService categoryService;

    @Autowired
    public RecipeServiceImpl(RecipeRepository recipeRepository, IngredientService ingredientService, CategoryService categoryService) {
        this.recipeRepository = recipeRepository;
        this.ingredientService = ingredientService;
        this.categoryService = categoryService;
    }
    @Override
    public Recipe createRecipe(RecipeDto recipeDto) {

        Category category = validateAndSetCategory(recipeDto.getCategory());

        List<RecipeIngredient> recipeIngredients = validateAndSetIngredientsWithQuantities(recipeDto.getIngredients());

        Recipe recipe = new Recipe();
        recipe.setName(recipeDto.getName());
        recipe.setCategory(category);
        recipe.setImageData(recipeDto.getImageData());
        recipe.setCookingTime(recipeDto.getCookingTime());
        recipe.setRecipeIngredients(recipeIngredients);

        return recipeRepository.save(recipe);
    }


    @Override
    public void updateRecipe(Long id, RecipeDto updatedRecipeDto) {
        Optional<Recipe> existingRecipeOptional = recipeRepository.findById(id);

        if (existingRecipeOptional.isPresent()) {
            Recipe existingRecipe = existingRecipeOptional.get();

            Category updatedCategory = validateAndSetCategory(updatedRecipeDto.getCategory());
            existingRecipe.setCategory(updatedCategory);

            List<RecipeIngredient> updatedIngredients = validateAndSetIngredients(updatedRecipeDto.getIngredients());
            existingRecipe.setRecipeIngredients(updatedIngredients);

            existingRecipe.setName(updatedRecipeDto.getName());
            existingRecipe.setImageData(updatedRecipeDto.getImageData());
            existingRecipe.setCookingTime(updatedRecipeDto.getCookingTime());

            recipeRepository.save(existingRecipe);
        } else {
            throw new RecipeNotFoundException("Recipe with ID " + id + " not found");
        }
    }

    @Override
    public List<RecipeIngredient> getIngredientsByIds(List<Long> ingredientIds) {
        return ingredientService.getIngredientsByIds(ingredientIds);
    }

    @Override
    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe with ID " + id + " not found"));
    }

    @Override
    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id);

    }

    private Category validateAndSetCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }


        return category;
    }

    private List<RecipeIngredient> validateAndSetIngredientsWithQuantities(List<RecipeIngredientDto> ingredientDtos) {
        List<RecipeIngredient> recipeIngredients = new ArrayList<>();

        for (RecipeIngredientDto ingredientDto : ingredientDtos) {
            if (ingredientDto.getQuantity() < 0) {
                throw new IllegalArgumentException("Quantity cannot be negative");
            }

            RecipeIngredient recipeIngredient = ingredientService.convertDtoToEntity(ingredientDto);
            recipeIngredients.add(recipeIngredient);
        }

        return recipeIngredients;
    }

    private List<RecipeIngredient> validateAndSetIngredients(List<RecipeIngredientDto> ingredientDtos) {
        List<RecipeIngredient> recipeIngredients = new ArrayList<>();

        for (RecipeIngredientDto ingredientDto : ingredientDtos) {
            RecipeIngredient recipeIngredient = ingredientService.convertDtoToEntity(ingredientDto);
            recipeIngredients.add(recipeIngredient);
        }
        return recipeIngredients;
    }
}
