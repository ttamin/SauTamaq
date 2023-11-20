package com.example.sautamaq.service;

import com.example.sautamaq.dto.RecipeDto;
import com.example.sautamaq.exception.RecipeNotFoundException;
import com.example.sautamaq.model.Category;
import com.example.sautamaq.model.Recipe;
import com.example.sautamaq.repository.RecipeRepository;
import com.example.sautamaq.service.impl.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeServiceImpl implements RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Override
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }
    @Override

    public Optional<Recipe> getRecipeById(Long id) {
        return recipeRepository.findById(id);
    }
    @Override
    public Recipe createRecipe(RecipeDto recipeDto, MultipartFile imageFile) {
        try {
            Recipe recipe = new Recipe();
            recipe.setName(recipeDto.getName());
            recipe.setCookingTime(recipeDto.getCookingTime());
            recipe.setId(recipeDto.getCategory().getId());
            recipe.setIngredients(recipeDto.getIngredients());

            if (imageFile != null) {
                byte[] imageData = imageFile.getBytes();
                recipe.setImageData(imageData);
            }
            return recipeRepository.save(recipe);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image file", e);
        }
    }
    @Override
    public void updateRecipeImage(Long recipeId, MultipartFile file) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found with id: " + recipeId));
        try {
            byte[] imageData = file.getBytes();
            recipe.setImageData(imageData);
            recipeRepository.save(recipe);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file", e);
        }
    }

}
