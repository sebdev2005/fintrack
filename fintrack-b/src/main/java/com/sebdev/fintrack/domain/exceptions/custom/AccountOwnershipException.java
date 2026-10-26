package com.sebdev.fintrack.domain.exceptions.custom;

public class AccountOwnershipException extends RuntimeException {
    public AccountOwnershipException(String message) {
        super(message);
    }
}
