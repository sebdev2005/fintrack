package com.sebdev.fintrack.application.ports;

import com.sebdev.fintrack.domain.models.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepositoryPort {
    Category save(Category category);
    List<Category> categories(UUID idUser);
    Optional<Category> findById(UUID id);
}
