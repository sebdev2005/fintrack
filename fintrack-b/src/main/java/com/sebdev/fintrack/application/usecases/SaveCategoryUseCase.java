package com.sebdev.fintrack.application.usecases;

import com.sebdev.fintrack.application.ports.CategoryRepositoryPort;
import com.sebdev.fintrack.domain.models.Category;

import java.time.LocalDateTime;
import java.util.UUID;

public class SaveCategoryUseCase {

    private final CategoryRepositoryPort port;

    SaveCategoryUseCase(CategoryRepositoryPort port){
        this.port = port;
    }

    public Category execute(String name, UUID idUser){
        Category category = Category.builder()
                .id(UUID.randomUUID())
                .name(name)
                .idUser(idUser)
                .createdAt(LocalDateTime.now())
                .active(true).build();

        return port.save(category);
    }
}
