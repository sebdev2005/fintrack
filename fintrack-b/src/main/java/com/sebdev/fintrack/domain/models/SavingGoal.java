package com.sebdev.fintrack.domain.models;

import com.sebdev.fintrack.domain.exceptions.custom.InvalidSavingGoalException;
import com.sebdev.fintrack.domain.exceptions.custom.SavingGoalOwnershipException;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;


public record SavingGoal(UUID id, UUID idUser, String purpose, BigDecimal targetAmount, BigDecimal currentAmount, LocalDate deadline, BigDecimal periodicContribution, boolean active, Instant createdAt) {

    @Builder
    public SavingGoal{
        if (idUser == null) {
            throw new InvalidSavingGoalException("La meta de ahorro debe tener un usuario propietario");
        }
        if (purpose == null || purpose.isBlank() || purpose.length() > 60) {
            throw new InvalidSavingGoalException("El propósito no puede estar vacío ni superar los 60 caracteres");
        }
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidSavingGoalException("El monto objetivo debe ser mayor que cero");
        }
        if (currentAmount == null || currentAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidSavingGoalException("El monto actual debe ser mayor o igual que cero");
        }
        if (periodicContribution != null && periodicContribution.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidSavingGoalException("La contribución periódica debe ser mayor o igual que cero");
        }
    }
    public SavingGoal deactivate(UUID requesterId) {
        if (requesterId == null) {
            throw new InvalidSavingGoalException("El usuario solicitante no puede ser nulo");
        }
        if (!idUser.equals(requesterId)) {
            throw new SavingGoalOwnershipException("La meta de ahorro no pertenece a este usuario");
        }
        if (!active) {
            return this;
        }
        return new SavingGoal(id, idUser, purpose, targetAmount, currentAmount, deadline, periodicContribution, false, createdAt);
    }
    public boolean isOwnedBy(UUID userId) {
        return idUser.equals(userId);
    }
    public SavingGoal contribute(BigDecimal amount){
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidSavingGoalException("El aporte debe ser mayor que cero");
        }
        if (!active) {
            throw new InvalidSavingGoalException("No se puede aportar a una meta inactiva");
        }
        return new SavingGoal(id, idUser, purpose, targetAmount, currentAmount.add(amount), deadline, periodicContribution, active ,createdAt);
    }


}
