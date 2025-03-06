package com.example.user_service.exceptions;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);  // ✅ Truyền message vào Exception
    }
}
