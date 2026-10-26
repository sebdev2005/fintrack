package com.sebdev.fintrack.domain.exceptions;

public record ErrorResponse(int status, String message, String timestamp) {
}
