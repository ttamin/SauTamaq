package com.example.sautamaq.service;

import com.example.sautamaq.dto.CategoryDto;
import com.example.sautamaq.exception.CategoryAlreadyExistsException;
import com.example.sautamaq.exception.CategoryNotFoundException;
import com.example.sautamaq.exception.RecipeNotFoundException;
import com.example.sautamaq.model.Category;
import com.example.sautamaq.repository.CategoryRepository;
import com.example.sautamaq.service.impl.CategoryService;
import com.example.sautamaq.service.impl.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, ImageService imageService) {
        this.categoryRepository = categoryRepository;
        this.imageService = imageService;
    }

    @Override
    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public Category addCategory(CategoryDto categoryDto) {
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new CategoryAlreadyExistsException("Category with that name is already exists");
        }
        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setActive(true);
        return categoryRepository.save(category);
    }

    @Override
    public void removeCategoryById(long id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isPresent()) {
            categoryRepository.deleteById(id);
        } else {
            throw new CategoryNotFoundException("Category with ID " + id + " not found");
        }
    }

    @Override
    public void updateCategory(long id, Category updatedCategory) {
        Optional<Category> existingCategoryOptional = categoryRepository.findById(id);
        if (existingCategoryOptional.isPresent()) {
                Category existingCategory = existingCategoryOptional.get();
                existingCategory.setName(updatedCategory.getName());
                existingCategory.setActive(updatedCategory.isActive());
                categoryRepository.save(existingCategory);
        } else {
            throw new CategoryNotFoundException("Category with ID " + id + " not found");
        }
    }

    @Override
    public Category getCategoryById(long id) {
        return categoryRepository.findById(id).orElse(null);
    }


    @Async
    @Override
    public CompletableFuture<Void> uploadRecipeImageAsync(Long categoryId, MultipartFile file) {
        try{
            String imagePath = imageService.uploadCategoryImage(file, categoryId);
            byte[] imageBytes = file.getBytes();
            uploadRecipeImage(categoryId, imagePath, imageBytes);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    public void uploadRecipeImage(Long categoryId, String imagePath, byte[] imageBytes) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found with id: " + categoryId));
        category.setImagePath(imagePath);
        category.setImageData(imageBytes);
        categoryRepository.save(category);
    }
}
