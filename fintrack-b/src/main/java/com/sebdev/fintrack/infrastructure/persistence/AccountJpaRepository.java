package com.sebdev.fintrack.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, UUID> {

    @Query("SELECT a FROM AccountEntity a WHERE (a.idUser = : idUser) AND a.active = TRUE")
    List<AccountEntity> accounts(@Param("idUser") UUID idUser);

}
