package com.sebdev.fintrack.domain.models;

import com.sebdev.fintrack.domain.exceptions.custom.CategoryOwnershipException;
import com.sebdev.fintrack.domain.exceptions.custom.InvalidCategoryException;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Category {

    private final UUID id;
    private final String name;
    private final UUID idUser;
    private final Instant createdAt;
    private final boolean active;

    @Builder
    public Category(UUID id, String name, UUID idUser, Instant createdAt, boolean active){
        if (name == null || name.isBlank()) {
            throw new InvalidCategoryException("El nombre de la categoría no puede estar vacío");
        }
        if (name.length() > 40) {
            throw new InvalidCategoryException("El nombre de la categoría no puede superar los 40 caracteres");
        }
        this.id = id;
        this.name = name;
        this.idUser = idUser;
        this.createdAt = createdAt;
        this.active = active;
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
}
