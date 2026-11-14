package com.sebdev.fintrack.infrastructure.persistence;

import com.sebdev.fintrack.domain.models.types.MovementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Query("""
       SELECT COALESCE(SUM(CASE WHEN m.type = :income THEN m.amount ELSE -m.amount END), 0)
       FROM MovementEntity m
       WHERE m.idAccount = :idAccount
       """)
    BigDecimal netByAccount(@Param("idAccount") UUID idAccount,
                            @Param("income") MovementType income);


    @Query("""
    SELECT COALESCE(SUM(m.amount), 0)
    FROM MovementEntity m
    WHERE m.type = :type
      AND m.occurredOn >= :startDate AND m.occurredOn < :endDate
      AND m.idAccount IN (SELECT a.id FROM AccountEntity a WHERE a.idUser = :idUser)
    """)
    BigDecimal totalByType(@Param("idUser") UUID idUser, @Param("type") MovementType type,
                           @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


    @Query("""
    SELECT m.idCategory, SUM(m.amount)
    FROM MovementEntity m
    WHERE m.type = :type
      AND m.occurredOn >= :startDate AND m.occurredOn < :endDate
      AND m.idAccount IN (SELECT a.id FROM AccountEntity a WHERE a.idUser = :idUser)
    GROUP BY m.idCategory
    ORDER BY SUM(m.amount) DESC
    """)
    List<Object[]> expensesByCategory(@Param("idUser") UUID idUser,
                                      @Param("type") MovementType type,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);
}