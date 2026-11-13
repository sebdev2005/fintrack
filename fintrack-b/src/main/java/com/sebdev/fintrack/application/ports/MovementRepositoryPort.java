package com.sebdev.fintrack.application.ports;

import com.sebdev.fintrack.domain.models.Movement;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MovementRepositoryPort {
    Movement save(Movement movement);
    Optional<Movement> findById(UUID idMovement);
    List<Movement> findByUser(UUID idUser);
    List<Movement> findByAccount(UUID idAccount);
    BigDecimal netByAccount(UUID idAccount);

}
