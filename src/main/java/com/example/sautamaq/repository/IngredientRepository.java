package com.example.sautamaq.repository;

import com.example.sautamaq.model.Ingredient;
import com.example.sautamaq.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Optional<Ingredient> findById(Long IngredientId);
}
