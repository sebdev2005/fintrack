package com.sebdev.fintrack.infrastructure.persistence;


import com.sebdev.fintrack.domain.models.AccountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    private UUID idUser;

    @NotBlank
    @Size(max = 40)
    private String name;

    @Enumerated(EnumType.STRING)
    @NotBlank
    private AccountType type;

    @Column(name = "initial_balance")
    @NotBlank
    private BigDecimal initialBalance;

    private String color;

    private String bank;

    @Column
    private boolean active;

    @Column(name = "created_at")
    private Instant createdAt;

}
