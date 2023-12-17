package com.example.sautamaq.controller;

import com.example.sautamaq.dto.RecipeDto;
import com.example.sautamaq.exception.RecipeNotFoundException;
import com.example.sautamaq.model.Ingredient;
import com.example.sautamaq.model.Recipe;
import com.example.sautamaq.service.impl.ImageService;
import com.example.sautamaq.service.impl.IngredientService;
import com.example.sautamaq.service.impl.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/recipe")
public class RecipeController {
    private final RecipeService recipeService;
    private final ImageService imageService;
    private final AsyncTaskExecutor asyncTaskExecutor;
    private final IngredientService ingredientService;



    @Autowired
    public RecipeController(RecipeService recipeService, ImageService imageService, AsyncTaskExecutor asyncTaskExecutor, IngredientService ingredientService) {
        this.recipeService = recipeService;
        this.imageService = imageService;
        this.asyncTaskExecutor = asyncTaskExecutor;
        this.ingredientService = ingredientService;
    }

    @PostMapping("/create")
    public ResponseEntity<Recipe> createRecipe(@RequestBody RecipeDto recipeDto) {
        Recipe createdRecipe = recipeService.createRecipe(recipeDto);
        return new ResponseEntity<>(createdRecipe, HttpStatus.CREATED);
    }

    @PostMapping("/uploadImage/{id}")
    public DeferredResult<ResponseEntity<String>> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        CompletableFuture<Void> uploadFuture = recipeService.uploadRecipeImageAsync(id, file);

        DeferredResult<ResponseEntity<String>> deferredResult = new DeferredResult<>();

        uploadFuture.whenCompleteAsync((result, ex) -> {
            if (ex != null) {
                deferredResult.setErrorResult(ex);
            } else {
                deferredResult.setResult(ResponseEntity.ok("Image uploaded successfully."));
            }
        }, asyncTaskExecutor);

        return deferredResult;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDto> getRecipeById(@PathVariable Long id) {
        RecipeDto recipeDto = recipeService.getRecipeById(id);
        if (recipeDto != null) {
            return ResponseEntity.ok(recipeDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/update/{recipeId}")
    public ResponseEntity<RecipeDto> updateRecipeWithoutImage(
            @PathVariable Long recipeId,
            @RequestBody RecipeDto updatedRecipeDto) {
        Recipe updatedRecipe = recipeService.updateRecipeWithoutImage(recipeId, updatedRecipeDto);
        RecipeDto updatedRecipeResponse = recipeService.convertRecipeToDto(updatedRecipe);
        return ResponseEntity.ok(updatedRecipeResponse);
    }

    @GetMapping("/ingredient/{id}")
    public ResponseEntity<Ingredient> getIngredientById(@PathVariable Long id) {
        Ingredient ingredient = ingredientService.getIngredientById(id);

        if (ingredient != null) {
            return ResponseEntity.ok(ingredient);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/delete/{recipeId}")
    public ResponseEntity<String> deleteRecipe(@PathVariable Long recipeId) {
        try {
            recipeService.deleteRecipe(recipeId);
            return new ResponseEntity<>("Рецепт успешно удален", HttpStatus.OK);
        } catch (RecipeNotFoundException e) {
            return new ResponseEntity<>("Рецепт с ID " + recipeId + " не найден", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Ошибка при удалении рецепта. Обратитесь к администратору.", HttpStatus.BAD_REQUEST);
        }
    }

}
