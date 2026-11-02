package com.sebdev.fintrack.config;

import com.sebdev.fintrack.application.ports.AccountRepositoryPort;
import com.sebdev.fintrack.application.ports.CategoryRepositoryPort;
import com.sebdev.fintrack.application.ports.MovementRepositoryPort;
import com.sebdev.fintrack.application.usecases.*;
import com.sebdev.fintrack.domain.models.Movement;
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

    @Bean
    DeactivateCategoryUseCase deactivateCategoryUseCase(CategoryRepositoryPort categoryRepositoryPort){
        return new DeactivateCategoryUseCase(categoryRepositoryPort);
    }

    @Bean
    SaveAccountUseCase saveAccountUseCase(AccountRepositoryPort accountRepositoryPort){
        return new SaveAccountUseCase(accountRepositoryPort);
    }

    @Bean
    ListAccountsUseCase listAccountsUseCase(AccountRepositoryPort accountRepositoryPort){
        return new ListAccountsUseCase(accountRepositoryPort);
    }

    @Bean
    DeactivateAccountUseCase deactivateAccountUseCase(AccountRepositoryPort accountRepositoryPort){
        return new DeactivateAccountUseCase(accountRepositoryPort);
    }

    @Bean
    SaveMovementUseCase saveMovementUseCase(AccountRepositoryPort accountRepositoryPort, CategoryRepositoryPort categoryRepositoryPort, MovementRepositoryPort movementRepositoryPort){
        return new SaveMovementUseCase(accountRepositoryPort,categoryRepositoryPort,movementRepositoryPort);
    }

    @Bean
    ListMovementForAccountUseCase listMovementForAccountUseCase(AccountRepositoryPort accountRepositoryPort, MovementRepositoryPort movementRepositoryPort){
        return new ListMovementForAccountUseCase(accountRepositoryPort, movementRepositoryPort);
    }

    @Bean
    ListMovementForUserUseCase listMovementForUserUseCase(MovementRepositoryPort movementRepositoryPort){
        return new ListMovementForUserUseCase(movementRepositoryPort);
    }

}
