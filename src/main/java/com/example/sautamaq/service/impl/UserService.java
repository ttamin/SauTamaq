package com.example.sautamaq.service.impl;

import com.example.sautamaq.dto.RecipeDto;
import com.example.sautamaq.dto.UserDto;
import com.example.sautamaq.model.Recipe;
import com.example.sautamaq.model.User;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface UserService {
    User createAdmin(User user);
    List<User> getAllUsers();
    User getUserByEmail(String email);
    void registerUser(UserDto userDto);
    void deleteUserById(Long id);
    // TODO: changePassword, changeRole, updatePassword, forgotPassword
    User addToFavorites(Long userId, Long recipeId);
    User removeFromFavorites(Long userId, Long recipeId);
    List<RecipeDto> getAllFavoriteRecipes(Long userId);
    Recipe getFavoriteRecipe(Long userId, Long recipeId);


}
