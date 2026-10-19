package com.sebdev.fintrack.application;

import com.sebdev.fintrack.application.ports.CategoryRepositoryPort;
import com.sebdev.fintrack.application.usecases.ListCategoriesUseCase;
import com.sebdev.fintrack.application.usecases.SaveCategoryUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ApplicationConfiguration {

    @Bean
    SaveCategoryUseCase saveCategoryUseCase(CategoryRepositoryPort categoryRepositoryPort) {
        return new SaveCategoryUseCase(categoryRepositoryPort);
    }

    @Bean
    ListCategoriesUseCase listCategoriesUseCase(CategoryRepositoryPort categoryRepositoryPort) {
        return new ListCategoriesUseCase(categoryRepositoryPort);
    }
}
