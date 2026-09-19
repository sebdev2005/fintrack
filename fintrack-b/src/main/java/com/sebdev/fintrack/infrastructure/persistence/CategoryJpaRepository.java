package com.sebdev.fintrack.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, UUID> {

    @Query("SELECT c FROM CategoryEntity c WHERE(c.idUser = :idUser or c.idUser IS NULL) AND c.active = TRUE")
    List<CategoryEntity> categories(@Param("idUser") UUID idUser);
}
