package com.example.sautamaq.exception;

import java.io.IOException;

public class ImageUploadException extends RuntimeException {
    public ImageUploadException(String message, IOException e) {
        super(message);
    }
}
