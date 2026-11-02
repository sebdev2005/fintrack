package com.sebdev.fintrack.domain.models;

import com.sebdev.fintrack.domain.exceptions.custom.InvalidMovementException;
import com.sebdev.fintrack.domain.models.types.MovementType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record Movement(UUID id, UUID idAccount, UUID idCategory, MovementType type, BigDecimal amount,
                       LocalDate occurredOn, String note, Instant createdAt) {
    @Builder
    public Movement {
        if (idAccount == null) {
            throw new InvalidMovementException("El movimiento tiene que pertenecer a una cuenta");
        }
        if (type == null) {
            throw new InvalidMovementException("El tipo de movimiento es obligatorio");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidMovementException("El monto debe ser mayor que cero");
        }
        if (occurredOn == null) {
            throw new InvalidMovementException("La fecha es obligatoria");
        }
        if (note != null && note.length() > 200) {
            throw new InvalidMovementException("La nota no puede superar los 200 caracteres");
        }

    }

}
