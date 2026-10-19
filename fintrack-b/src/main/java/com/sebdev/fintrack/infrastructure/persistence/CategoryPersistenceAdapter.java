package com.sebdev.fintrack.infrastructure.persistence;

import com.sebdev.fintrack.application.ports.CategoryRepositoryPort;
import com.sebdev.fintrack.domain.models.Category;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class CategoryPersistenceAdapter implements CategoryRepositoryPort {

    private final CategoryJpaRepository repository;

    public CategoryPersistenceAdapter(CategoryJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Category save(Category category) {
        CategoryEntity savedEntity = repository.save(toEntity(category));
        return toDomain(savedEntity);
    }

    @Override
    public List<Category> categories(UUID idUser) {
        return repository.categories(idUser).stream()
                .map(CategoryPersistenceAdapter::toDomain)
                .toList();
    }

    private static CategoryEntity toEntity(Category category) {
        return new CategoryEntity(
                category.getId(),
                category.getName(),
                category.getIdUser(),
                category.getCreatedAt(),
                category.isActive()
        );
    }

    private static Category toDomain(CategoryEntity entity) {
        return Category.builder()
                .id(entity.getId())
                .name(entity.getName())
                .idUser(entity.getIdUser())
                .createdAt(entity.getCreatedAt())
                .active(entity.isActive())
                .build();
    }
}
