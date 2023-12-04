package com.example.sautamaq.controller;

import com.example.sautamaq.dto.RecipeDto;
import com.example.sautamaq.exception.RecipeNotFoundException;
import com.example.sautamaq.model.Recipe;
import com.example.sautamaq.service.impl.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recipe")
public class RecipeController {
    private final RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }


    @PostMapping("/create")
    public ResponseEntity<Recipe> createRecipe(@RequestBody RecipeDto recipeDto) {
        Recipe createdRecipe = recipeService.createRecipe(recipeDto);
        return new ResponseEntity<>(createdRecipe, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
        Recipe recipe = recipeService.getRecipeById(id);
        if (recipe != null) {
            return new ResponseEntity<>(recipe, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateRecipe(@PathVariable Long id, @RequestBody RecipeDto updatedRecipeDto) {
        try {
            recipeService.updateRecipe(id, updatedRecipeDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RecipeNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        try {
            recipeService.deleteRecipe(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RecipeNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
