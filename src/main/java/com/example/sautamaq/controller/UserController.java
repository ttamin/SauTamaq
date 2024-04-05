package com.example.sautamaq.controller;

import com.example.sautamaq.dto.RecipeDto;
import com.example.sautamaq.model.Recipe;
import com.example.sautamaq.model.User;
import com.example.sautamaq.service.impl.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "exp://192.168.0.14:8081")

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String Hi(){
        return "hi ";
    }
    @PostMapping("/register/admin")
    public User registerAdmin(@RequestBody User user) {
        return userService.createAdmin(user);
    }

    @GetMapping("/users/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long userId) {
        try {
            userService.deleteUserById(userId);
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{userId}/add-fav")
    public ResponseEntity<User> addToFavorites(@PathVariable Long userId, @RequestBody Map<String, Long> request) {
        Long recipeId = request.get("recipeId");

        if (recipeId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User updatedUser = userService.addToFavorites(userId, recipeId);

        if (updatedUser != null) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{userId}/remove-fav")
    public ResponseEntity<User> removeFromFavorites(@PathVariable Long userId, @RequestBody Map<String, Long> request) {
        Long recipeId = request.get("recipeId");

        if (recipeId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User updatedUser = userService.removeFromFavorites(userId, recipeId);

        if (updatedUser != null) {
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{userId}/favorite-recipes")
    public ResponseEntity<List<RecipeDto>> getAllFavoriteRecipes(@PathVariable Long userId) {
        List<RecipeDto> favoriteRecipes = userService.getAllFavoriteRecipes(userId);

        if (!favoriteRecipes.isEmpty()) {
            return new ResponseEntity<>(favoriteRecipes, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{userId}/favorite-recipes/{recipeId}")
    public ResponseEntity<Recipe> getFavoriteRecipe(@PathVariable Long userId, @PathVariable Long recipeId) {
        Recipe favoriteRecipe = userService.getFavoriteRecipe(userId, recipeId);

        if (favoriteRecipe != null) {
            return new ResponseEntity<>(favoriteRecipe, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
