package com.example.sautamaq.service;

import com.example.sautamaq.dto.RecipeDto;
import com.example.sautamaq.dto.UserDto;
import com.example.sautamaq.exception.NotFoundException;
import com.example.sautamaq.exception.UserAlreadyExistsException;
import com.example.sautamaq.exception.UserNotExistsException;
import com.example.sautamaq.model.Recipe;
import com.example.sautamaq.model.User;
import com.example.sautamaq.repository.RecipeRepository;
import com.example.sautamaq.repository.UserRepository;
import com.example.sautamaq.service.impl.UserService;
import com.example.sautamaq.util.RecipeMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RecipeRepository recipeRepository;

    @Override
    public User createAdmin(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("User with that email is already registered");
        }

        user.setRole("ADMIN");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotExistsException("There is no user with such email"));
    }

    @Override
    public void registerUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistsException("User with this email already exists");
        }
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole("USER");
        user.setActive(true);
        userRepository.save(user);
    }

    @Override
    public void deleteUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            userRepository.delete(userOptional.get());
        } else {
            // Обработка случая, когда пользователя с заданным ID нет
            throw new EntityNotFoundException("User with ID " + id + " not found");
        }

    }

    @Override
    public User addToFavorites(Long userId, Long recipeId) {
        User user = userRepository.findById(userId).orElse(null);
        Recipe recipe = recipeRepository.findById(recipeId).orElse(null);

        if (user != null && recipe != null) {
            List<Recipe> favorites = user.getFavorites();

            if (!favorites.contains(recipe)) {
                favorites.add(recipe);
                user.setFavorites(favorites);
                userRepository.save(user);
            }

            return user;
        }
        else if (!userRepository.existsById(userId)){
            throw new UserNotExistsException("User with this ID not found");
        } else if (!recipeRepository.existsById(recipeId)) {
            throw new NotFoundException("Recipe with this ID not found");
        }

        return null;
    }

    @Override
        public User removeFromFavorites(Long userId, Long recipeId) {
            User user = userRepository.findById(userId).orElse(null);
            Recipe recipe = recipeRepository.findById(recipeId).orElse(null);

            if (user != null && recipe != null) {
                List<Recipe> favorites = user.getFavorites();

                if (favorites.contains(recipe)) {
                    favorites.remove(recipe);
                    user.setFavorites(favorites);
                    userRepository.save(user);
                }

                return user;
            }

            return null;
        }
    @Override
    public List<RecipeDto> getAllFavoriteRecipes(Long userId) {
        User user = userRepository.findById(userId).orElse(null);

        if (user != null) {
            List<Recipe> favoriteRecipes = user.getFavorites();
            return RecipeMapper.INSTANCE.toDtoList(favoriteRecipes);
        }

        return Collections.emptyList();
    }
    @Override
    public Recipe getFavoriteRecipe(Long userId, Long recipeId) {
        User user = userRepository.findById(userId).orElse(null);

        if (user != null) {
            List<Recipe> favoriteRecipes = user.getFavorites();

            for (Recipe recipe : favoriteRecipes) {
                if (recipe.getId().equals(recipeId)) {
                    return recipe;
                }
            }
        }

        return null;
    }

}

