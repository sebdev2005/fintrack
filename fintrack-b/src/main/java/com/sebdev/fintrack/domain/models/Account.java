package com.sebdev.fintrack.domain.models;

import com.sebdev.fintrack.domain.exceptions.custom.AccountOwnershipException;
import com.sebdev.fintrack.domain.exceptions.custom.InvalidAccountException;
import com.sebdev.fintrack.domain.models.types.AccountType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Account(UUID id, UUID idUser, String name, AccountType type, BigDecimal initialBalance, String color,
                      String bank, boolean active, Instant createdAt) {
    @Builder
    public Account {
        if (name == null || name.isBlank()) {
            throw new InvalidAccountException("El nombre de la cuenta no puede estar vacío");
        }
        if (name.length() > 40) {
            throw new InvalidAccountException("El nombre de la cuenta no puede superar los 40 caracteres");
        }
        if (idUser == null) {
            throw new InvalidAccountException("La cuenta debe estar asociada a un usuario");
        }
        if (type == null) {
            throw new InvalidAccountException("El tipo de cuenta es obligatorio");
        }
        if (initialBalance == null) {
            throw new InvalidAccountException("El saldo inicial de la cuenta es obligatorio");
        }

    }

    public Account deactivate(UUID requesterId) {
        if (requesterId == null) {
            throw new InvalidAccountException("El usuario solicitante no puede ser nulo");
        }
        if (!idUser.equals(requesterId)) {
            throw new AccountOwnershipException("La cuenta no pertenece a este usuario");
        }
        if (!active) {
            return this;
        }
        return new Account(id, idUser, name, type, initialBalance, color, bank, false, createdAt);
    }

    public boolean isOwnedBy(UUID userId) {
        return idUser.equals(userId);
    }
}