package com.example.sautamaq.controller;

import com.example.sautamaq.dto.CategoryDto;
import com.example.sautamaq.model.Category;
import com.example.sautamaq.service.CategoryServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    private final CategoryServiceImpl categoryService;

    public CategoryController(CategoryServiceImpl categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/add")
    public ResponseEntity<Category> createCategory(@RequestBody CategoryDto categoryDto){
        Category category = categoryService.addCategory(categoryDto);
        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public List<Category> getAllCategory(){
        return categoryService.getAllCategory();
    }

    @PostMapping("/delete")
    public String deleteCategorybyId(@RequestBody CategoryDto categoryDto){
        categoryService.removeCategoryById(categoryDto.getId());
        return "deleted";
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable long id, @RequestBody Category updatedCategory) {
            categoryService.updateCategory(id, updatedCategory);
            return ResponseEntity.ok("Category updated successfully");
    }

    //that can be used for user
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable long id) {
        Category category = categoryService.getCategoryById(id);

        if (category != null && category.isActive()) {
            return ResponseEntity.ok(category);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}