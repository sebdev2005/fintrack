package com.sebdev.fintrack.application.usecases;

import com.sebdev.fintrack.application.ports.SavingGoalRepositoryPort;
import com.sebdev.fintrack.domain.exceptions.custom.ResourceNotFoundException;
import com.sebdev.fintrack.domain.models.SavingGoal;

import java.util.UUID;

public class DeactivateSavingGoalUseCase {

    private final SavingGoalRepositoryPort port;

    public DeactivateSavingGoalUseCase(SavingGoalRepositoryPort port) {
        this.port = port;
    }

    public SavingGoal execute(UUID idSavingGoal, UUID idUser) {
        SavingGoal savingGoal = port.findById(idSavingGoal)
                .orElseThrow(() -> new ResourceNotFoundException("No existe este ahorro para este usuario"));
        SavingGoal deactivated = savingGoal.deactivate(idUser);
        return port.save(deactivated);
    }


}
