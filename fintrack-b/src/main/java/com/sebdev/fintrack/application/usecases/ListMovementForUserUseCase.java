package com.sebdev.fintrack.application.usecases;

import com.sebdev.fintrack.application.ports.MovementRepositoryPort;
import com.sebdev.fintrack.domain.models.Movement;

import java.util.List;
import java.util.UUID;

public class ListMovementForUserUseCase {

    private final MovementRepositoryPort movementRepositoryPort;

    public ListMovementForUserUseCase(MovementRepositoryPort movementRepositoryPort) {
        this.movementRepositoryPort = movementRepositoryPort;
    }

    public List<Movement> execute(UUID idUser){
        return movementRepositoryPort.findByUser(idUser);
    }
}
