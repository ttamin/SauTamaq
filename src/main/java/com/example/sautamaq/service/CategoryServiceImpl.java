package com.example.sautamaq.service;

import com.example.sautamaq.dto.CategoryDto;
import com.example.sautamaq.exception.CategoryAlreadyExistsException;
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
    public List<Category> getAllCategory(){
        return categoryRepository.findAll();
    }
    @Override
    public void addCategory(CategoryDto categoryDto){
        if(categoryRepository.existsByName(categoryDto.getName())){
            throw new CategoryAlreadyExistsException("Category with than name is already exists");
        }
        Category category = new Category();
        category.setName(categoryDto.getName());
        categoryRepository.save(category);
    }
    @Override
    public void removeCategoryById(long id){
        categoryRepository.deleteById(id);
    }
    @Override
    public Optional<Category> getCategoryById(long id){
        return categoryRepository.findById(id);
    }
}
