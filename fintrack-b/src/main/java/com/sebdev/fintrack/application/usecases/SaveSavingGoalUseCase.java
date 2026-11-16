package com.sebdev.fintrack.application.usecases;

import com.sebdev.fintrack.application.ports.SavingGoalRepositoryPort;
import com.sebdev.fintrack.domain.exceptions.custom.InvalidSavingGoalException;
import com.sebdev.fintrack.domain.models.SavingGoal;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class SaveSavingGoalUseCase {
    public record Command( String purpose, BigDecimal targetAmount, LocalDate deadline, BigDecimal periodicContribution){

    }
    private final SavingGoalRepositoryPort savingGoalRepositoryPort;

    public SaveSavingGoalUseCase(SavingGoalRepositoryPort savingGoalRepositoryPort) {
        this.savingGoalRepositoryPort = savingGoalRepositoryPort;
    }

    public SavingGoal execute(Command command, UUID idUser){

        if (command.deadline() != null && command.deadline().isBefore(LocalDate.now())) {
            throw new InvalidSavingGoalException("La fecha límite no puede estar en el pasado");
        }
        SavingGoal savingGoal =  SavingGoal.builder()
                .id(UUID.randomUUID())
                .idUser(idUser)
                .purpose(command.purpose)
                .targetAmount(command.targetAmount)
                .currentAmount(BigDecimal.ZERO)
                .deadline(command.deadline)
                .periodicContribution(command.periodicContribution)
                .active(true)
                .createdAt(Instant.now())
                .build();

        return savingGoalRepositoryPort.save(savingGoal);

    }
}
