package com.example.sautamaq.service;

import com.example.sautamaq.dto.CategoryDto;
import com.example.sautamaq.dto.IngredientDto;
import com.example.sautamaq.dto.InstructionDto;
import com.example.sautamaq.dto.RecipeDto;
import com.example.sautamaq.exception.CategoryAlreadyExistsException;
import com.example.sautamaq.exception.NotFoundException;
import com.example.sautamaq.exception.RecipeNotFoundException;
import com.example.sautamaq.model.Category;
import com.example.sautamaq.model.Ingredient;
import com.example.sautamaq.model.Instruction;
import com.example.sautamaq.model.Recipe;
import com.example.sautamaq.repository.CategoryRepository;
import com.example.sautamaq.repository.IngredientRepository;
import com.example.sautamaq.repository.RecipeRepository;
import com.example.sautamaq.service.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    private final IngredientService ingredientService;
    private final CategoryService categoryService;
    private final ImageService imageService;
    private final CategoryRepository categoryRepository;



    @Autowired
    public RecipeServiceImpl(RecipeRepository recipeRepository, IngredientRepository ingredientRepository, IngredientService ingredientService, CategoryService categoryService, ImageService imageService, CategoryRepository categoryRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.ingredientService = ingredientService;
        this.categoryService = categoryService;
        this.imageService = imageService;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Recipe createRecipe(RecipeDto recipeDto) {
        Objects.requireNonNull(recipeDto, "RecipeDto cannot be null");

        Category category = validateAndSetCategory(recipeDto.getCategory());

        Recipe recipe = new Recipe();
        recipe.setName(recipeDto.getName());
        recipe.setCategory(category);
        recipe.setCookingTime(recipeDto.getCookingTime());
        recipe.setLevel(recipeDto.getLevel());

        List<Ingredient> recipeIngredients = createRecipeIngredients(recipeDto.getIngredients());
        recipe.setRecipeIngredients(recipeIngredients);
        for (Ingredient ingredient : recipeIngredients) {
            ingredient.setRecipe(recipe);
        }

        List<Instruction> recipeInstructions = createRecipeInstructions(recipeDto.getInstructions());
        recipe.setRecipeInstructions(recipeInstructions);
        for (Instruction instruction : recipeInstructions) {
            instruction.setRecipe(recipe);
        }

        return recipeRepository.save(recipe);
    }

    private List<Ingredient> createRecipeIngredients(List<IngredientDto> ingredientDtos) {
        List<Ingredient> recipeIngredients = new ArrayList<>();

        if (ingredientDtos != null) {
            for (IngredientDto ingredientDto : ingredientDtos) {
                Ingredient recipeIngredient = new Ingredient();
                recipeIngredient.setName(ingredientDto.getName());
                recipeIngredient.setQuantity(ingredientDto.getQuantity());
                recipeIngredients.add(recipeIngredient);
            }
        }

        return recipeIngredients;
    }

    private List<Instruction> createRecipeInstructions(List<InstructionDto> instructionDtos) {
        List<Instruction> recipeInstructions = new ArrayList<>();

        if (instructionDtos != null) {
            for (InstructionDto instructionDto : instructionDtos) {
                Instruction recipeInstruction = new Instruction();
                recipeInstruction.setName(instructionDto.getName());
                recipeInstruction.setStep(instructionDto.getStep());
                recipeInstructions.add(recipeInstruction); // Add this line
            }
        }

        return recipeInstructions;
    }

    private Category validateAndSetCategory(CategoryDto categoryDto) {
        if (categoryDto == null) {
            return null; // or throw an exception, depending on your requirements
        }

        Optional<Category> existingCategory = categoryRepository.findByName(categoryDto.getName());
        Category category;
        if (existingCategory.isPresent()) {
            category = existingCategory.get();
        } else {
            // Check if the category exists by name
            if (categoryRepository.existsByName(categoryDto.getName())) {
                throw new CategoryAlreadyExistsException("Category with that name already exists");
            }

            // If not, add a new category using your existing method
            category = categoryService.addCategory(categoryDto);
        }

        return category;
    }



    @Async
    @Override
    public CompletableFuture<Void> uploadRecipeImageAsync(Long recipeId, MultipartFile file) {
        try {
            String imagePath = imageService.uploadImage(file, recipeId);
            byte[] imageBytes = file.getBytes();
            uploadRecipeImage(recipeId, imagePath, imageBytes);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    public void uploadRecipeImage(Long recipeId, String imagePath, byte[] imageBytes) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found with id: " + recipeId));
        recipe.setImagePath(imagePath);
        recipe.setImageData(imageBytes);
        recipeRepository.save(recipe);
    }

    @Override
    public RecipeDto getRecipeById(Long id) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(id);

        if (recipeOptional.isPresent()) {
            Recipe recipe = recipeOptional.get();
            RecipeDto recipeDto = convertRecipeToDto(recipe);
            return recipeDto;
        } else {
            throw new RecipeNotFoundException("Recipe with ID " + id + " not found");
        }
    }
    @Override
    public RecipeDto convertRecipeToDto(Recipe recipe) {
        String imageData = recipe.getImageData() != null ?
                Base64.getEncoder().encodeToString(recipe.getImageData()) : null;
        return new RecipeDto(
                        recipe.getId(),
                        recipe.getName(),
                        convertCategoryToDto(recipe.getCategory()),
                        recipe.getImagePath(),
                        recipe.getImageData(),
                        recipe.getCookingTime(),
                convertIngredientsToDto(recipe.getRecipeIngredients()),
                convertInstructionsToDto(recipe.getRecipeInstructions()),
                recipe.getLevel())
                ;
    }
    private CategoryDto convertCategoryToDto(Category category) {
        return new CategoryDto(category.getId(), category.getName(), category.isActive(), category.getImagePath(), category.getImageData());
    }

    private List<IngredientDto> convertIngredientsToDto(List<Ingredient> ingredients) {
        return ingredients.stream()
                .map(this::convertIngredientToDto)
                .collect(Collectors.toList());
    }

    private IngredientDto convertIngredientToDto(Ingredient ingredient) {
        return new IngredientDto(ingredient.getId(), ingredient.getName(), ingredient.getQuantity());
    }
    private List<InstructionDto> convertInstructionsToDto(List<Instruction> instructions) {
        return instructions.stream()
                .map(this::convertInstructionToDto)
                .collect(Collectors.toList());
    }

    private InstructionDto convertInstructionToDto(Instruction instruction) {
        return new InstructionDto(instruction.getId(), instruction.getName(),instruction.getStep());
    }

    @Override
    public Recipe updateRecipeWithoutImage(Long recipeId, RecipeDto updatedRecipeDto) {
        Objects.requireNonNull(updatedRecipeDto, "Updated RecipeDto cannot be null");

        if (!recipeId.equals(updatedRecipeDto.getId())) {
            throw new IllegalArgumentException("Provided recipe ID does not match the ID in the updatedRecipeDto");
        }

        Recipe existingRecipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found with id: " + recipeId));

        Category updatedCategory = validateAndSetCategory(updatedRecipeDto.getCategory());
        existingRecipe.setName(updatedRecipeDto.getName());
        existingRecipe.setCategory(updatedCategory);
        existingRecipe.setCookingTime(updatedRecipeDto.getCookingTime());
        existingRecipe.setLevel(updatedRecipeDto.getLevel());

        updateRecipeIngredients(existingRecipe.getRecipeIngredients(), updatedRecipeDto.getIngredients());
        updateRecipeInstructions(existingRecipe.getRecipeInstructions(), updatedRecipeDto.getInstructions());

        return recipeRepository.save(existingRecipe);
    }

    private void updateRecipeIngredients(List<Ingredient> existingIngredients, List<IngredientDto> updatedIngredientDtos) {
        if (updatedIngredientDtos != null) {
            for (IngredientDto updatedIngredientDto : updatedIngredientDtos) {
                Long ingredientId = updatedIngredientDto.getId();
                Optional<Ingredient> existingIngredientOptional = existingIngredients.stream()
                        .filter(existingIngredient -> existingIngredient.getId().equals(ingredientId))
                        .findFirst();

                if (existingIngredientOptional.isPresent()) {
                    Ingredient existingIngredient = existingIngredientOptional.get();
                    updateIngredient(existingIngredient, updatedIngredientDto);
                }
            }
        }
    }

    private void updateRecipeInstructions(List<Instruction> existingInstructions, List<InstructionDto> updatedInstructionDtos) {
        if (updatedInstructionDtos != null) {
            for (InstructionDto updatedInstructionDto : updatedInstructionDtos) {
                Long instructionId = updatedInstructionDto.getId();
                Optional<Instruction> existingInstructionOptional = existingInstructions.stream()
                        .filter(existingInstruction -> existingInstruction.getId().equals(instructionId))
                        .findFirst();

                if (existingInstructionOptional.isPresent()) {
                    Instruction existingInstruction = existingInstructionOptional.get();
                    updateInstruction(existingInstruction, updatedInstructionDto);
                }
            }
        }
    }

    private void updateIngredient(Ingredient existingIngredient, IngredientDto updatedIngredientDto) {
        existingIngredient.setName(updatedIngredientDto.getName());
        existingIngredient.setQuantity(updatedIngredientDto.getQuantity());
    }

    private void updateInstruction(Instruction existingInstruction, InstructionDto updatedInstructionDto) {
        existingInstruction.setName(updatedInstructionDto.getName());
        existingInstruction.setStep(updatedInstructionDto.getStep());
    }




//    @Override
//    @Transactional
//    public Recipe updateRecipe(Long recipeId, Recipe updatedRecipe) {
//        Recipe existingRecipe = recipeRepository.findById(recipeId)
//                .orElseThrow(() -> new RecipeNotFoundException("Рецепт с ID " + recipeId + " не найден"));
//
//        // Обновление данных рецепта
//        updateRecipeDetails(existingRecipe, updatedRecipe);
//
//        // Сохранение обновленного рецепта
//        recipeRepository.save(existingRecipe);
//
//        System.out.println("Рецепт успешно обновлен: " + existingRecipe);
//
//        return existingRecipe;
//    }
//
//    @Override
//    @Transactional
//    public void updateIngredients(Long recipeId, List<Ingredient> updatedIngredients) {
//        Recipe existingRecipe = recipeRepository.findById(recipeId)
//                .orElseThrow(() -> new RecipeNotFoundException("Рецепт с ID " + recipeId + " не найден"));
//
//        existingRecipe.getRecipeIngredients().forEach(existingIngredient -> {
//            if (!updatedIngredients.contains(existingIngredient)) {
//                existingIngredient.setRecipe(null); // Удаление связи с рецептом
//            }
//        });
//
//        existingRecipe.getRecipeIngredients().removeIf(existingIngredient -> !updatedIngredients.contains(existingIngredient));
//
//        updatedIngredients.forEach(updatedIngredient -> {
//            if (!existingRecipe.getRecipeIngredients().contains(updatedIngredient)) {
//                updatedIngredient.setRecipe(existingRecipe); // Установка связи с рецептом
//                existingRecipe.getRecipeIngredients().add(updatedIngredient);
//            }
//        });
//
//        System.out.println("Ингредиенты обновлены: " + existingRecipe.getRecipeIngredients());
//    }
//
//    @Override
//    @Transactional
//    public void updateInstructions(Long recipeId, List<Instruction> updatedInstructions) {
//        Recipe existingRecipe = recipeRepository.findById(recipeId)
//                .orElseThrow(() -> new RecipeNotFoundException("Рецепт с ID " + recipeId + " не найден"));
//
//        existingRecipe.getRecipeInstructions().forEach(existingInstruction -> {
//            if (!updatedInstructions.contains(existingInstruction)) {
//                existingInstruction.setRecipe(null); // Удаление связи с рецептом
//            }
//        });
//
//        existingRecipe.getRecipeInstructions().removeIf(existingInstruction -> !updatedInstructions.contains(existingInstruction));
//
//        updatedInstructions.forEach(updatedInstruction -> {
//            if (!existingRecipe.getRecipeInstructions().contains(updatedInstruction)) {
//                updatedInstruction.setRecipe(existingRecipe); // Установка связи с рецептом
//                existingRecipe.getRecipeInstructions().add(updatedInstruction);
//            }
//        });
//
//        System.out.println("Инструкции обновлены: " + existingRecipe.getRecipeInstructions());
//    }
//
//    private void updateRecipeDetails(Recipe existingRecipe, Recipe updatedRecipe) {
//        existingRecipe.setName(updatedRecipe.getName());
//        existingRecipe.setCategory(updatedRecipe.getCategory());
//        existingRecipe.setImagePath(updatedRecipe.getImagePath());
//        existingRecipe.setImageData(updatedRecipe.getImageData());
//        existingRecipe.setCookingTime(updatedRecipe.getCookingTime());
//        existingRecipe.setLevel(updatedRecipe.getLevel());
//    }


    @Override
    @Transactional
    public void deleteRecipe(Long recipeId) {
        try {
            recipeRepository.deleteById(recipeId);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении рецепта. Обратитесь к администратору.", e);
        }
    }

}