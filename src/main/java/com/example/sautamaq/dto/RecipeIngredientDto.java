package com.example.sautamaq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeIngredientDto {
    private Long id;
    private IngredientDto ingredient;
    private int quantity;
}
