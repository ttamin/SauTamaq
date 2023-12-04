package com.example.sautamaq.repository;

import com.example.sautamaq.model.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {
    Optional<RecipeIngredient> findById(Long recipeIngredientId);
}
