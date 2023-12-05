package com.example.sautamaq.service;

import com.example.sautamaq.dto.CategoryDto;
import com.example.sautamaq.exception.CategoryAlreadyExistsException;
import com.example.sautamaq.exception.CategoryNotFoundException;
import com.example.sautamaq.model.Category;
import com.example.sautamaq.repository.CategoryRepository;
import com.example.sautamaq.service.impl.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
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
}
