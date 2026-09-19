package com.sebdev.fintrack.domain.models;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Category {

    private final UUID id;
    private final String name;
    private final UUID idUser;
    private final LocalDateTime createdAt;
    private final boolean active;

    @Builder
    public Category(UUID id, String name, UUID idUser, LocalDateTime createdAt, boolean active){
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("el nombre no puede estar vacio");
        }
        this.id = id;
        this.name = name;
        this.idUser = idUser;
        this.createdAt = createdAt;
        this.active = active;
    }
}
