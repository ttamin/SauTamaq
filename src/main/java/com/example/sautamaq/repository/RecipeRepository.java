package com.example.sautamaq.repository;

import com.example.sautamaq.model.Category;
import com.example.sautamaq.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Optional<Recipe> findById(Long recipeId);


    List<Recipe> findByCategory(Category category);
}
