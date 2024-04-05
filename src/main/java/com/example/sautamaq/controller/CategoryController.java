package com.example.sautamaq.controller;

import com.example.sautamaq.dto.CategoryDto;
import com.example.sautamaq.model.Category;
import com.example.sautamaq.service.CategoryServiceImpl;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@CrossOrigin(origins = "exp://192.168.8.254:8081")
@RestController
@RequestMapping("/category")
public class CategoryController {
    private final CategoryServiceImpl categoryService;
    private final AsyncTaskExecutor asyncTaskExecutor;

    public CategoryController(CategoryServiceImpl categoryService, AsyncTaskExecutor asyncTaskExecutor) {
        this.categoryService = categoryService;
        this.asyncTaskExecutor = asyncTaskExecutor;
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

    @PostMapping("/delete/{id}")
    public String deleteCategorybyId(@RequestBody CategoryDto categoryDto){
        categoryService.removeCategoryById(categoryDto.getId());
        return "deleted";
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable long id, @RequestBody Category updatedCategory) {
            categoryService.updateCategory(id, updatedCategory);
            return ResponseEntity.ok("Category updated successfully");
    }

    @PostMapping("/uploadImage/{id}")
    public DeferredResult<ResponseEntity<String>> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        CompletableFuture<Void> uploadFuture = categoryService.uploadRecipeImageAsync(id, file);

        DeferredResult<ResponseEntity<String>> deferredResult = new DeferredResult<>();

        uploadFuture.whenCompleteAsync((result, ex) -> {
            if (ex != null) {
                deferredResult.setErrorResult(ex);
            } else {
                deferredResult.setResult(ResponseEntity.ok("Image uploaded successfully."));
            }
        }, asyncTaskExecutor);

        return deferredResult;
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