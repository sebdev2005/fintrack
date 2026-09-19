package com.sebdev.fintrack.infrastructure.persistence;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(length = 40)
    private String name;

    @Column(name = "id_user")
    private UUID idUser;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column
    private boolean active;

}
