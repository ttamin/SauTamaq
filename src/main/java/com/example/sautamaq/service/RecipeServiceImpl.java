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
import java.util.Objects;
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
        Objects.requireNonNull(recipeDto, "RecipeDto cannot be null");

        Category category = validateAndSetCategory(recipeDto.getCategory());

        Recipe recipe = new Recipe();
        recipe.setName(recipeDto.getName());
        recipe.setCategory(category);
        recipe.setCookingTime(recipeDto.getCookingTime());

        List<Ingredient> recipeIngredients = createRecipeIngredients(recipeDto.getIngredients());
        recipe.setRecipeIngredients(recipeIngredients);
        for (Ingredient ingredient : recipeIngredients) {
            ingredient.setRecipe(recipe);
        }
        return recipeRepository.save(recipe);
    }

    private List<Ingredient> createRecipeIngredients(List<IngredientDto> ingredientDtos) {
        List<Ingredient> recipeIngredients = new ArrayList<>();

        if (ingredientDtos != null) {
            for (IngredientDto ingredientDto : ingredientDtos) {
                Ingredient recipeIngredient = new Ingredient();
                recipeIngredient.setName(ingredientDto.getName());
                recipeIngredient.setQuantity(ingredientDto.getQuantity());
                recipeIngredients.add(recipeIngredient);
            }
        }

        return recipeIngredients;
    }

    private Category validateAndSetCategory(Category category) {
        return Objects.requireNonNull(category, "Category cannot be null");
    }


    @Override
    public void updateRecipeImage(Long recipeId, String imagePath) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found with id: " + recipeId));

        recipe.setImagePath(imagePath);
        recipeRepository.save(recipe);
    }

}