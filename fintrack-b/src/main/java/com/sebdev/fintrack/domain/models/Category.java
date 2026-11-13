package com.sebdev.fintrack.domain.models;

import com.sebdev.fintrack.domain.exceptions.custom.CategoryOwnershipException;
import com.sebdev.fintrack.domain.exceptions.custom.InvalidCategoryException;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

public record Category(UUID id, String name, UUID idUser, Instant createdAt, boolean active) {

    @Builder
    public Category {
        if (name == null || name.isBlank()) {
            throw new InvalidCategoryException("El nombre de la categoría no puede estar vacío");
        }
        if (name.length() > 40) {
            throw new InvalidCategoryException("El nombre de la categoría no puede superar los 40 caracteres");
        }
    }

    public Category deactivated(UUID requesterId) {
        if (requesterId == null) {
            throw new InvalidCategoryException("El usuario solicitante no puede ser nulo");
        }
        if (!idUser.equals(requesterId)) {
            throw new CategoryOwnershipException("La categoría no pertenece a este usuario");
        }
        return new Category(id, name, idUser, createdAt, false);
    }

    public boolean isAvailableFor(UUID userId) {
        return active && (idUser == null || idUser.equals(userId));
    }
}
