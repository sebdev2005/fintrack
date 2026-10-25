package com.sebdev.fintrack.domain.exceptions.custom;

public class CategoryOwnershipException extends RuntimeException {
    public CategoryOwnershipException(String message) {
        super(message);
    }
}
