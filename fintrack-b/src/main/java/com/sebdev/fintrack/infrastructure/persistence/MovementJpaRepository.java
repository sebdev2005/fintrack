package com.sebdev.fintrack.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MovementJpaRepository extends JpaRepository<MovementEntity, UUID> {

    List<MovementEntity> findByIdAccountOrderByOccurredOnDesc(UUID idAccount);

    @Query("""
    SELECT m FROM MovementEntity m
    WHERE m.idAccount IN (SELECT a.id FROM AccountEntity a WHERE a.idUser = :idUser)
    ORDER BY m.occurredOn DESC
    """)
    List<MovementEntity> findByUser(@Param("idUser") UUID idUser);
}
