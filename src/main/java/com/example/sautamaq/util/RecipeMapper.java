package com.example.sautamaq.util;

import com.example.sautamaq.dto.RecipeDto;
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

    RecipeDto toDto(Recipe recipe);

    List<RecipeDto> toDtoList(List<Recipe> recipes);
}