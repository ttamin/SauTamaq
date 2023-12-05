package com.example.sautamaq.controller;

import com.example.sautamaq.dto.RecipeDto;
import com.example.sautamaq.model.Recipe;
import com.example.sautamaq.service.impl.ImageService;
import com.example.sautamaq.service.impl.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/recipe")
public class RecipeController {
    private final RecipeService recipeService;
    private final ImageService imageService;

    @Autowired
    public RecipeController(RecipeService recipeService, ImageService imageService) {
        this.recipeService = recipeService;
        this.imageService = imageService;
    }

    @PostMapping("/create")
    public ResponseEntity<Recipe> createRecipe(@RequestBody RecipeDto recipeDto) {
        Recipe createdRecipe = recipeService.createRecipe(recipeDto);
        return new ResponseEntity<>(createdRecipe, HttpStatus.CREATED);
    }

    @PostMapping("/uploadImage/{recipeId}")
    public ResponseEntity<String> uploadImage(@PathVariable Long recipeId, @RequestParam("file") MultipartFile file) {
        String imagePath = imageService.uploadImage(file, recipeId);
        recipeService.updateRecipeImage(recipeId, imagePath);
        return ResponseEntity.ok("Image uploaded successfully.");
    }


}
