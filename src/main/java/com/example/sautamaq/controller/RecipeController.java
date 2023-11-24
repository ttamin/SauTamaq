package com.example.sautamaq.controller;

import com.example.sautamaq.dto.RecipeDto;
import com.example.sautamaq.model.Recipe;
import com.example.sautamaq.service.RecipeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/recipe")
public class RecipeController {

    @Autowired
    private RecipeServiceImpl recipeService;

    @PostMapping("/create")
    public ResponseEntity<Recipe> createRecipe(@RequestBody RecipeDto recipeDto,
                                               @RequestParam("imageFile") MultipartFile imageFile) {
        Recipe createdRecipe = recipeService.createRecipe(recipeDto, imageFile);
        return new ResponseEntity<>(createdRecipe, HttpStatus.CREATED);
    }

}
