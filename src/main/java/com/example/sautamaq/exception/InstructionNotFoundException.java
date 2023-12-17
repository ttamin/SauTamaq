package com.example.sautamaq.exception;

public class InstructionNotFoundException extends RuntimeException {
    public InstructionNotFoundException(String message) {
        super(message);
    }
}
