package com.example.sautamaq.util;

import com.example.sautamaq.dto.CategoryDto;
import com.example.sautamaq.dto.IngredientDto;
import com.example.sautamaq.dto.InstructionDto;
import com.example.sautamaq.dto.RecipeDto;
import com.example.sautamaq.model.Category;
import com.example.sautamaq.model.Ingredient;
import com.example.sautamaq.model.Instruction;
import com.example.sautamaq.model.Recipe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper
public interface RecipeMapper {
    RecipeMapper INSTANCE = Mappers.getMapper(RecipeMapper.class);

    @Mapping(target = "id", source = "recipe.id")
    @Mapping(target = "name", source = "recipe.name")
    @Mapping(target = "imagePath", source = "recipe.imagePath")
    @Mapping(target = "cookingTime", source = "recipe.cookingTime")
    @Mapping(target = "level", source = "recipe.level")
    @Mapping(target = "ingredients", source = "recipe.recipeIngredients")
    @Mapping(target = "instructions", source = "recipe.recipeInstructions")
    @Mapping(target = "category", source = "recipe.category") // Добавлено маппирование категории
    RecipeDto toDto(Recipe recipe);

    @Mapping(target = "id", source = "ingredient.id")
    IngredientDto mapIngredientToDto(Ingredient ingredient);

    @Mapping(target = "id", source = "instruction.id")
    InstructionDto mapInstructionToDto(Instruction instruction);

    CategoryDto mapCategoryToDto(Category category); // Добавлен метод для маппинга категории

    List<RecipeDto> toDtoList(List<Recipe> recipes);
}