package com.example.sautamaq.service;

import com.example.sautamaq.dto.CategoryDto;
import com.example.sautamaq.dto.IngredientDto;
import com.example.sautamaq.dto.RecipeDto;
import com.example.sautamaq.exception.RecipeNotFoundException;
import com.example.sautamaq.model.Category;
import com.example.sautamaq.model.Ingredient;
import com.example.sautamaq.model.Recipe;
import com.example.sautamaq.repository.IngredientRepository;
import com.example.sautamaq.repository.RecipeRepository;
import com.example.sautamaq.service.impl.CategoryService;
import com.example.sautamaq.service.impl.ImageService;
import com.example.sautamaq.service.impl.IngredientService;
import com.example.sautamaq.service.impl.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    private final IngredientService ingredientService;
    private final CategoryService categoryService;
    private final ImageService imageService;

    @Autowired
    public RecipeServiceImpl(RecipeRepository recipeRepository, IngredientRepository ingredientRepository, IngredientService ingredientService, CategoryService categoryService, ImageService imageService) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.ingredientService = ingredientService;
        this.categoryService = categoryService;
        this.imageService = imageService;
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

    private Category validateAndSetCategory(CategoryDto categoryDto) {
        Objects.requireNonNull(categoryDto, "CategoryDto cannot be null");

        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());
        category.setActive(categoryDto.isActive());

        return category;
    }


    @Async
    @Override
    public CompletableFuture<Void> uploadRecipeImageAsync(Long recipeId, MultipartFile file) {
        try {
            String imagePath = imageService.uploadImage(file, recipeId);
            byte[] imageBytes = file.getBytes();
            uploadRecipeImage(recipeId, imagePath, imageBytes);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Override
    public void uploadRecipeImage(Long recipeId, String imagePath, byte[] imageBytes) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found with id: " + recipeId));
        recipe.setImagePath(imagePath);
        recipe.setImageData(imageBytes);
        recipeRepository.save(recipe);
    }

    @Override
    public RecipeDto getRecipeById(Long id) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(id);

        if (recipeOptional.isPresent()) {
            Recipe recipe = recipeOptional.get();
            RecipeDto recipeDto = convertRecipeToDto(recipe);
            return recipeDto;
        } else {
            throw new RecipeNotFoundException("Recipe with ID " + id + " not found");
        }
    }
    private RecipeDto convertRecipeToDto(Recipe recipe) {
        String imageData = recipe.getImageData() != null ?
                Base64.getEncoder().encodeToString(recipe.getImageData()) : null;
        return new RecipeDto(
                recipe.getId(),
                recipe.getName(),
                convertCategoryToDto(recipe.getCategory()),
                recipe.getImagePath(),
                recipe.getImageData(),
                recipe.getCookingTime(),
                convertIngredientsToDto(recipe.getRecipeIngredients())
        );
    }
    private CategoryDto convertCategoryToDto(Category category) {
        return new CategoryDto(category.getId(), category.getName(), category.isActive());
    }

    private List<IngredientDto> convertIngredientsToDto(List<Ingredient> ingredients) {
        return ingredients.stream()
                .map(this::convertIngredientToDto)
                .collect(Collectors.toList());
    }

    private IngredientDto convertIngredientToDto(Ingredient ingredient) {
        return new IngredientDto(ingredient.getId(), ingredient.getName(), ingredient.getQuantity());
    }



}