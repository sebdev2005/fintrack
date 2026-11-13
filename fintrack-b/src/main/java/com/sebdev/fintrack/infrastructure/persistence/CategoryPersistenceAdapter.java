package com.sebdev.fintrack.infrastructure.persistence;

import com.sebdev.fintrack.application.ports.CategoryRepositoryPort;
import com.sebdev.fintrack.domain.models.Category;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
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

    @Override
    public Optional<Category> findById(UUID id) {
        return repository.findById(id)
                .map(CategoryPersistenceAdapter::toDomain);
    }

    private static CategoryEntity toEntity(Category category) {
        return new CategoryEntity(
                category.id(),
                category.name(),
                category.idUser(),
                category.createdAt(),
                category.active()
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
