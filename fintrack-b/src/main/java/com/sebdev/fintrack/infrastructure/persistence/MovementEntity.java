package com.sebdev.fintrack.infrastructure.persistence;

import com.sebdev.fintrack.domain.models.types.MovementType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "movement")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class MovementEntity {

    @Id
    private UUID id;

    @NotNull
    private UUID idAccount;

    private UUID idCategory;

    @NotNull
    private MovementType type;

    @NotNull
    @DecimalMin(value = "0.0", message = "El monto no puede ser negativo")
    private BigDecimal amount;

    @NotNull
    @NotBlank
    private LocalDate occurredOn;

    @NotNull
    @Size(max = 200)
    private String note;

    @NotNull
    @NotBlank
    private Instant createdAt;
}
