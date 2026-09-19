package com.sebdev.fintrack.application.usecases;

import com.sebdev.fintrack.application.ports.CategoryRepositoryPort;
import com.sebdev.fintrack.domain.models.Category;

import java.util.List;
import java.util.UUID;

public class ListCategoriesUseCase {

    private final CategoryRepositoryPort port;

    public ListCategoriesUseCase(CategoryRepositoryPort port){
        this.port = port;
    }

    public List<Category> execute(UUID idUser){
        return port.categories(idUser);
    }
}
