package com.sebdev.fintrack.infrastructure.persistence;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "saving_goal")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SavingGoalEntity {


    @Id
    private UUID id;

    @NotNull
    private UUID idUser;

    @NotBlank
    @Size(max = 60)
    private String purpose;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "target_amount")
    private BigDecimal targetAmount;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "current_amount")
    private BigDecimal currentAmount;

    private LocalDate deadline;

    @Column(name = "periodic_amount")
    private BigDecimal periodicAmount;

    @NotNull
    private boolean active;

    @Column(name = "created_at")
    private Instant createdAt;

}
