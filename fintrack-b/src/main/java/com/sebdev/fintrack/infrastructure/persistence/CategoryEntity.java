package com.sebdev.fintrack.infrastructure.persistence;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "category")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class CategoryEntity {

    @Id
    private UUID id;

    @NotBlank
    @Size(max = 40)
    @Column(length = 40)
    private String name;

    @Column(name = "id_user")
    private UUID idUser;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column
    private boolean active;

}
