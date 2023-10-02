package com.example.employeedirectoryproject.config.exception;

public class NotExistEmployeeException extends RuntimeException{
    public NotExistEmployeeException(String message) {
        super(message);
    }
}
