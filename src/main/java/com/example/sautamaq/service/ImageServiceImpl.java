package com.example.sautamaq.service;

import com.example.sautamaq.service.impl.ImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageServiceImpl implements ImageService {
    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public String uploadImage(MultipartFile file, Long recipeId) {
        try {
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName != null ?
                    originalFileName.substring(originalFileName.lastIndexOf(".")) : "";
            String fileName = recipeId + fileExtension;
            Path filePath = Paths.get(uploadPath, fileName);
            Files.write(filePath, file.getBytes());

            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }
}
