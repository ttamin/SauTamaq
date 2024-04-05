package com.example.sautamaq.service;

import com.example.sautamaq.dto.CategoryDto;
import com.example.sautamaq.dto.IngredientDto;
import com.example.sautamaq.dto.InstructionDto;
import com.example.sautamaq.dto.RecipeDto;
import com.example.sautamaq.exception.CategoryAlreadyExistsException;
import com.example.sautamaq.exception.RecipeNotFoundException;
import com.example.sautamaq.model.Category;
import com.example.sautamaq.model.Ingredient;
import com.example.sautamaq.model.Instruction;
import com.example.sautamaq.model.Recipe;
import com.example.sautamaq.repository.CategoryRepository;
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
    private final CategoryService categoryService;
    private final ImageService imageService;
    private final CategoryRepository categoryRepository;

    @Autowired
    public RecipeServiceImpl(RecipeRepository recipeRepository, CategoryService categoryService,
                             ImageService imageService, CategoryRepository categoryRepository) {
        this.recipeRepository = recipeRepository;
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
        recipe.setCalorie(recipeDto.getCalorie());

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
            String imagePath = imageService.uploadRecipeImage(file, recipeId);
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
                recipe.getLevel(),
                recipe.getCalorie())
                ;
    }


    @Override
    public List<IngredientDto> getIngredientsByRecipe(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        return recipe.getRecipeIngredients().stream()
                .map(this::convertIngredientToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InstructionDto> getInstructionsByRecipe(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        return recipe.getRecipeInstructions().stream()
                .map(this::convertInstructionToDto)
                .collect(Collectors.toList());
    }
    @Override
    public List<RecipeDto> getAllRecipes() {
        List<Recipe> recipes = recipeRepository.findAll();
        return recipes.stream()
                .map(this::convertRecipeToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecipeDto> getRecipesByCategory(Long categoryId) {
        Category category = new Category();
        category.setId(categoryId);

        List<Recipe> recipes = recipeRepository.findByCategory(category);

        return recipes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private RecipeDto convertToDto(Recipe recipe) {
        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setName(recipe.getName());
        recipeDto.setImageData(recipe.getImageData());
        recipeDto.setCookingTime(recipe.getCookingTime());
        return recipeDto;

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
        existingRecipe.setCalorie(updatedRecipeDto.getCalorie());

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