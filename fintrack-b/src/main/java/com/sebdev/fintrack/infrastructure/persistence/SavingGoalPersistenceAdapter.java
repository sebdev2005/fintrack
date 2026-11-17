package com.sebdev.fintrack.infrastructure.persistence;

import com.sebdev.fintrack.application.ports.SavingGoalRepositoryPort;
import com.sebdev.fintrack.domain.models.SavingGoal;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SavingGoalPersistenceAdapter implements SavingGoalRepositoryPort {

    private final SavingGoalJpaRepository repository;

    public SavingGoalPersistenceAdapter(SavingGoalJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public SavingGoal save(SavingGoal savingGoal) {
        SavingGoalEntity savingGoalEntity =repository.save(toEntity(savingGoal));
        return toDomain(savingGoalEntity);
    }

    @Override
    public Optional<SavingGoal> findById(UUID idSavingGoal) {
        return repository.findById(idSavingGoal)
                .map(SavingGoalPersistenceAdapter::toDomain);
    }

    @Override
    public List<SavingGoal> savingGoals(UUID idUser) {
        return repository.findByIdUserAndActiveTrueOrderByCreatedAtDesc(idUser)
                .stream().map(SavingGoalPersistenceAdapter::toDomain)
                .toList();
    }

    public static SavingGoalEntity toEntity(SavingGoal savingGoal){
        return new SavingGoalEntity(
                savingGoal.id(),
                savingGoal.idUser(),
                savingGoal.purpose(),
                savingGoal.targetAmount(),
                savingGoal.currentAmount(),
                savingGoal.deadline(),
                savingGoal.periodicContribution(),
                savingGoal.active(),
                savingGoal.createdAt()
        );
    }

    public static SavingGoal toDomain(SavingGoalEntity savingGoalEntity){
        return SavingGoal.builder()
                .id(savingGoalEntity.getId())
                .idUser(savingGoalEntity.getIdUser())
                .purpose(savingGoalEntity.getPurpose())
                .targetAmount(savingGoalEntity.getTargetAmount())
                .currentAmount(savingGoalEntity.getCurrentAmount())
                .deadline(savingGoalEntity.getDeadline())
                .periodicContribution(savingGoalEntity.getPeriodicAmount())
                .active(savingGoalEntity.isActive())
                .createdAt(savingGoalEntity.getCreatedAt())
                .build();
    }
}
