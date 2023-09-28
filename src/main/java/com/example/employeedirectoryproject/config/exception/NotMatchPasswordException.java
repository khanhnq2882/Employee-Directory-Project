package com.example.employeedirectoryproject.config.exception;

public class NotMatchPasswordException extends RuntimeException{
    public NotMatchPasswordException(String message) {
        super(message);
    }
}
