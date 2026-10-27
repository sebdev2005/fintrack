package com.sebdev.fintrack.infrastructure.persistence;


import com.sebdev.fintrack.domain.models.types.AccountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "account")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccountEntity {

    @Id
    private UUID id;

    @Column(name = "id_user")
    @NotNull
    private UUID idUser;

    @NotBlank
    @Size(max = 40)
    private String name;

    @Enumerated(EnumType.STRING)
    @NotNull
    private AccountType type;

    @Column(name = "initial_balance")
    @NotNull
    private BigDecimal initialBalance;

    private String color;

    private String bank;

    @Column
    private boolean active;

    @Column(name = "created_at")
    private Instant createdAt;

}
