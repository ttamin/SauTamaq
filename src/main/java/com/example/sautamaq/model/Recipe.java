package com.example.sautamaq.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "recipes")
public class Recipe {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    @JsonIgnore
    private Category category;
    @Column(name = "image_path")
    private String imagePath;
    @Lob
    @Column(name = "image_data")
    private byte[] imageData;
    @Column(nullable = false)
    private int cookingTime;
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Ingredient> recipeIngredients = new ArrayList<>();
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Instruction> recipeInstructions = new ArrayList<>();
    @Column(nullable = false)
    private String level;
    @ManyToMany(mappedBy = "favorites")
    private List<User> favoritedByUsers = new ArrayList<>();
    private int calorie;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public List<Ingredient> getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(List<Ingredient> recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }

    public List<Instruction> getRecipeInstructions() {
        return recipeInstructions;
    }

    public void setRecipeInstructions(List<Instruction> recipeInstructions) {
        this.recipeInstructions = recipeInstructions;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<User> getFavoritedByUsers() {
        return favoritedByUsers;
    }

    public void setFavoritedByUsers(List<User> favoritedByUsers) {
        this.favoritedByUsers = favoritedByUsers;
    }

    public int getCalorie() {
        return calorie;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }
}
