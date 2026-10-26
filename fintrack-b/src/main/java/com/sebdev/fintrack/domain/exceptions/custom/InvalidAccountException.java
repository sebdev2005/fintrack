package com.sebdev.fintrack.domain.exceptions.custom;

public class InvalidAccountException extends RuntimeException {
    public InvalidAccountException(String message) {
        super(message);
    }
}
