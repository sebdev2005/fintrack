package com.sebdev.fintrack.domain.exceptions.custom;

public class FieldVoidException extends RuntimeException {
    public FieldVoidException(String message) {
        super(message);
    }
}
