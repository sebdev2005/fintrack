package com.sebdev.fintrack.application.usecases;

import com.sebdev.fintrack.application.ports.SavingGoalRepositoryPort;
import com.sebdev.fintrack.domain.models.SavingGoal;

import java.util.List;
import java.util.UUID;

public class ListSavingGoalsUseCase {

    private final SavingGoalRepositoryPort port;


    public ListSavingGoalsUseCase(SavingGoalRepositoryPort port) {
        this.port = port;
    }

    public List<SavingGoal> execute(UUID idUser){
        return port.savingGoals(idUser);
    }
}
