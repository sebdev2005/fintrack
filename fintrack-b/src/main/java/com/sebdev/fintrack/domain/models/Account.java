package com.sebdev.fintrack.domain.models;

import com.sebdev.fintrack.domain.exceptions.custom.AccountOwnershipException;
import com.sebdev.fintrack.domain.exceptions.custom.InvalidAccountException;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Account {
    private final UUID id;
    private final UUID idUser;
    private final String name;
    private final AccountType type;
    private final BigDecimal initialBalance;
    private final String color;
    private final String bank;
    private final boolean active;
    private final Instant createdAt;

    @Builder
    public Account(UUID id, UUID idUser, String name, AccountType type, BigDecimal initialBalance, String color, String bank, boolean active, Instant createdAt) {
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

        this.id = id;
        this.idUser = idUser;
        this.name = name;
        this.type = type;
        this.initialBalance = initialBalance;
        this.color = color;
        this.bank = bank;
        this.active = active;
        this.createdAt = createdAt;
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
}