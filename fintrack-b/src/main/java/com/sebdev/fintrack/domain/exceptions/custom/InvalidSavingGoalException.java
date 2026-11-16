package com.sebdev.fintrack.domain.exceptions.custom;

public class InvalidSavingGoalException extends RuntimeException {
    public InvalidSavingGoalException(String message) {
        super(message);
    }
}
