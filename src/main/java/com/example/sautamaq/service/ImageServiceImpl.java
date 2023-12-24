package com.example.sautamaq.service;

import com.example.sautamaq.exception.ImageUploadException;
import com.example.sautamaq.repository.RecipeRepository;
import com.example.sautamaq.service.impl.ImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class ImageServiceImpl implements ImageService {
    private final RecipeRepository recipeRepository;

    @Value("${upload.path}")
    private String uploadPath;
    private static final int RECIPE_IMAGE_WIDTH = 100;
    private static final int RECIPE_IMAGE_HEIGHT = 200;
    private static final int CATEGORY_IMAGE_WIDTH = 27;
    private static final int CATEGORY_IMAGE_HEIGHT = 27;

    public ImageServiceImpl(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public String uploadRecipeImage(MultipartFile file, Long recipeId) {
        return uploadImage(file, recipeId, RECIPE_IMAGE_WIDTH, RECIPE_IMAGE_HEIGHT);
    }

    @Override
    public String uploadCategoryImage(MultipartFile file, Long categoryId) {
        return uploadImage(file, categoryId, CATEGORY_IMAGE_WIDTH, CATEGORY_IMAGE_HEIGHT);
    }

    private String uploadImage(MultipartFile file, Long entityId, int targetWidth, int targetHeight) {
        try {
            String fileExtension = getFileExtension(file);
            String fileName = entityId + fileExtension;
            Path filePath = Paths.get(uploadPath, fileName);

            BufferedImage originalImage = ImageIO.read(file.getInputStream());

            if (isRecipe(entityId)) {
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    BufferedImage resizedImage = resizeImage(originalImage, targetWidth, targetHeight);

                    ImageIO.write(resizedImage, fileExtension.substring(1), baos);

                    Files.write(filePath, baos.toByteArray());
                }
            } else {
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            return fileName;
        } catch (IOException e) {
            throw new ImageUploadException("Failed to upload image", e);
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        resizedImage.createGraphics().drawImage(
                originalImage.getScaledInstance(targetWidth, targetHeight, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
        return resizedImage;
    }

    private String getFileExtension(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        return originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf(".")) : "";
    }

    private boolean isRecipe(Long entityId) {
        return entityId != null && recipeRepository != null && recipeRepository.findById(entityId).isPresent();
    }
}