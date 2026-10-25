package com.sebdev.fintrack.application.usecases;

import com.sebdev.fintrack.application.ports.CategoryRepositoryPort;
import com.sebdev.fintrack.domain.exceptions.custom.ResourceNotFoundException;
import com.sebdev.fintrack.domain.models.Category;

import java.util.UUID;

public class DeactivateCategoryUseCase {

    private CategoryRepositoryPort port;

    public DeactivateCategoryUseCase(CategoryRepositoryPort port){
        this.port = port;
    }

    public Category execute(UUID id, UUID idUser){
        Category category = port.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro la categoria"));
        category = category.deactivated(idUser);
        return port.save(category);

    }

}
