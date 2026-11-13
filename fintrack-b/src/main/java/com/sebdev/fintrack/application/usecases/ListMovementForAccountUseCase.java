package com.sebdev.fintrack.application.usecases;

import com.sebdev.fintrack.application.ports.AccountRepositoryPort;
import com.sebdev.fintrack.application.ports.MovementRepositoryPort;
import com.sebdev.fintrack.domain.exceptions.custom.ResourceNotFoundException;
import com.sebdev.fintrack.domain.models.Movement;

import java.util.List;
import java.util.UUID;

public class ListMovementForAccountUseCase {

    private final AccountRepositoryPort accountRepositoryPort;
    private final MovementRepositoryPort movementRepositoryPort;

    public ListMovementForAccountUseCase(AccountRepositoryPort accountRepositoryPort, MovementRepositoryPort movementRepositoryPort) {
        this.accountRepositoryPort = accountRepositoryPort;
        this.movementRepositoryPort = movementRepositoryPort;
    }

    public List<Movement> movements(UUID idAccount, UUID idUser){
        accountRepositoryPort.findById(idAccount)
                .filter((a)-> a.isOwnedBy(idUser))
                .orElseThrow(() -> new ResourceNotFoundException("No hay cuentas asociadas a este usuario"));

        return movementRepositoryPort.findByAccount(idAccount);
    }
}
