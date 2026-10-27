package com.sebdev.fintrack.application.ports;

import com.sebdev.fintrack.domain.models.Movement;

import java.util.UUID;

public interface MovementRepositoryPort {
    Movement save(Movement movement);
    Movement findById(UUID idMovement);
    Movement findByAccount(UUID idAccount);
}
