package com.sebdev.fintrack.infrastructure.persistence;

import com.sebdev.fintrack.application.ports.MovementRepositoryPort;
import com.sebdev.fintrack.application.results.CategoryTotal;
import com.sebdev.fintrack.domain.models.Movement;
import com.sebdev.fintrack.domain.models.types.MovementType;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MovementPersistenceAdapter implements MovementRepositoryPort {

    private final MovementJpaRepository movementJpaRepository;

    public MovementPersistenceAdapter(MovementJpaRepository movementJpaRepository) {
        this.movementJpaRepository = movementJpaRepository;
    }

    @Override
    public Movement save(Movement movement) {
        MovementEntity entity = movementJpaRepository.save(toEntity(movement));
        return toDomain(entity);
    }

    @Override
    public Optional<Movement> findById(UUID idMovement) {
        return movementJpaRepository.findById(idMovement)
                .map(MovementPersistenceAdapter::toDomain);
    }

    @Override
    public List<Movement> findByUser(UUID idUser) {
        return movementJpaRepository.findByUser(idUser)
                .stream().map(MovementPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    public List<Movement> findByAccount(UUID idAccount) {
        return movementJpaRepository.findByIdAccountOrderByOccurredOnDesc(idAccount)
                .stream().map(MovementPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    public BigDecimal netByAccount(UUID idAccount) {
        return movementJpaRepository.netByAccount(idAccount, MovementType.INCOME);
    }

    @Override
    public BigDecimal totalByType(UUID idUser, MovementType type, LocalDate startDate, LocalDate endDate) {
        return movementJpaRepository.totalByType(idUser,type,startDate,endDate);
    }

    @Override
    public List<CategoryTotal> expensesByCategory(UUID idUser, LocalDate startDate, LocalDate endDate) {
        return movementJpaRepository.expensesByCategory(idUser, MovementType.EXPENSE, startDate, endDate)
                .stream()
                .map(row -> new CategoryTotal((UUID) row[0], (BigDecimal) row[1]))
                .toList();
    }

    public static MovementEntity toEntity(Movement movement){
        return new MovementEntity(
                movement.id(),
                movement.idAccount(),
                movement.idCategory(),
                movement.type(),
                movement.amount(),
                movement.occurredOn(),
                movement.note(),
                movement.createdAt()
        );
    }

    public static Movement toDomain(MovementEntity movementEntity){
        return Movement.builder()
                .id(movementEntity.getId())
                .idAccount(movementEntity.getIdAccount())
                .idCategory(movementEntity.getIdCategory())
                .type(movementEntity.getType())
                .amount(movementEntity.getAmount())
                .occurredOn(movementEntity.getOccurredOn())
                .note(movementEntity.getNote())
                .createdAt(movementEntity.getCreatedAt())
                .build();
    }
}
