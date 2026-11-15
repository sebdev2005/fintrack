package com.sebdev.fintrack.application.ports;

import com.sebdev.fintrack.application.results.CategoryTotal;
import com.sebdev.fintrack.domain.models.Movement;
import com.sebdev.fintrack.domain.models.types.MovementType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MovementRepositoryPort {
    Movement save(Movement movement);
    Optional<Movement> findById(UUID idMovement);
    List<Movement> findByUser(UUID idUser);
    List<Movement> findByAccount(UUID idAccount);
    BigDecimal netByAccount(UUID idAccount);
    BigDecimal totalByType(UUID idUser, MovementType type,  LocalDate startDate, LocalDate endDate);
    List<CategoryTotal> expensesByCategory(UUID idUser, LocalDate startDate, LocalDate endDate);
}
