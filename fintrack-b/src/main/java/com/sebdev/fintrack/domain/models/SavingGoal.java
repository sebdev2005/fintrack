package com.sebdev.fintrack.domain.models;

import com.sebdev.fintrack.domain.exceptions.custom.AccountOwnershipException;
import com.sebdev.fintrack.domain.exceptions.custom.InvalidAccountException;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;


public record SavingGoal(UUID id, UUID idUser, String purpose, BigDecimal target_amount, BigDecimal current_amount, LocalDate deadline, BigDecimal periodic_contribution, boolean active, Instant createdAt) {

    @Builder
    public SavingGoal{
        if(idUser == null){
            throw new RuntimeException("Un ahorro tiene que tener un dueño");
        }
        if(purpose.isBlank() || purpose.length() > 60){
            throw new RuntimeException("El proposito no puede estar vacio y tiene que ser maximo de 60 caracteres");
        }
        if(target_amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new RuntimeException("La meta debe ser un monto mayor a 0");
        }
        if(current_amount.compareTo(BigDecimal.ZERO) < 0){
            throw new RuntimeException("La cantidad actual debe ser mayor o igual a 0");
        }
        if(periodic_contribution.compareTo(BigDecimal.ZERO) < 0){
            throw new RuntimeException("La contribucion debe ser mayor a 0 o simplemente no poner");
        }
    }
    public SavingGoal deactivate(UUID requesterId) {
        if (requesterId == null) {
            throw new InvalidAccountException("El usuario solicitante no puede ser nulo");
        }
        if (!idUser.equals(requesterId)) {
            throw new AccountOwnershipException("La cuenta no pertenece a este usuario");
        }
        if (!active) {
            return this;
        }
        return new SavingGoal(id, idUser, purpose, target_amount, current_amount, deadline, periodic_contribution, false, createdAt);
    }
    public boolean isOwnedBy(UUID userId) {
        return idUser.equals(userId);
    }
    public BigDecimal contribute(BigDecimal amount){
        return current_amount.add(amount);
    }


}
