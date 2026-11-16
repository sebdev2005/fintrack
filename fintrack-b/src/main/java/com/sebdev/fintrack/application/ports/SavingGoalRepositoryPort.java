package com.sebdev.fintrack.application.ports;

import com.sebdev.fintrack.domain.models.SavingGoal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SavingGoalRepositoryPort {
    SavingGoal save(SavingGoal savingGoal);
    Optional<SavingGoal> findById(UUID idSavingGoal);
    List<SavingGoal> savingGoals(UUID idUser);
}
