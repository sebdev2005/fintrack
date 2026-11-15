package com.sebdev.fintrack.infrastructure.persistence;

import com.sebdev.fintrack.domain.models.types.MovementType;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Column(name = "id_account", nullable = false)
    private UUID idAccount;

    @Column(name = "id_category")
    private UUID idCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType type;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "occurred_on", nullable = false)
    private LocalDate occurredOn;

    @Column(length = 200)
    private String note;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
