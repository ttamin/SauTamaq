package com.example.sautamaq.dto;

import com.example.sautamaq.model.Category;
import com.example.sautamaq.model.Ingredient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDto {
    private Long id;
    private String name;
    private Category category;
    private byte[] imageData;
    private int cookingTime;
    private List<Ingredient> ingredients;
}
