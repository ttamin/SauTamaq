package com.example.sautamaq.service.impl;

import com.example.sautamaq.dto.CategoryDto;
import com.example.sautamaq.model.Category;
import com.example.sautamaq.model.Recipe;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
        List<Category> getAllCategory();
        Category addCategory(CategoryDto categoryDto);
        void removeCategoryById(long id);

        void updateCategory(long id, Category updatedCategory);
        Category getCategoryById(long id);
}
