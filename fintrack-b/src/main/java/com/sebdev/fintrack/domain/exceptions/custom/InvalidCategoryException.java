package com.sebdev.fintrack.domain.exceptions.custom;

public class InvalidCategoryException extends RuntimeException {
    public InvalidCategoryException(String message) {
        super(message);
    }
}
