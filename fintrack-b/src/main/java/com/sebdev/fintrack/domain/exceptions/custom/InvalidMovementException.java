package com.sebdev.fintrack.domain.exceptions.custom;

public class InvalidMovementException extends RuntimeException {
    public InvalidMovementException(String message) {
        super(message);
    }
}
