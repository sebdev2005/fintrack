package com.sebdev.fintrack.application.usecases;

import com.sebdev.fintrack.application.ports.SavingGoalRepositoryPort;
import com.sebdev.fintrack.domain.exceptions.custom.ResourceNotFoundException;
import com.sebdev.fintrack.domain.models.SavingGoal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavingGoalUseCasesTests {
    @Mock SavingGoalRepositoryPort repository;

    @Test
    void saveCreatesGoalWithZeroCurrentAmountAndPersistsIt() {
        UUID userId = UUID.randomUUID();
        SaveSavingGoalUseCase useCase = new SaveSavingGoalUseCase(repository);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        SavingGoal result = useCase.execute(new SaveSavingGoalUseCase.Command("Viaje", new BigDecimal("500"),
                LocalDate.now().plusMonths(3), new BigDecimal("50")), userId);

        assertNotNull(result.id());
        assertEquals(userId, result.idUser());
        assertEquals(BigDecimal.ZERO, result.currentAmount());
        assertTrue(result.active());
        verify(repository).save(result);
    }

    @Test
    void listReturnsRepositoryGoals() {
        UUID userId = UUID.randomUUID();
        List<SavingGoal> goals = List.of(goal(userId));
        when(repository.savingGoals(userId)).thenReturn(goals);

        assertEquals(goals, new ListSavingGoalsUseCase(repository).execute(userId));
    }

    @Test
    void deactivateSavesOwnedGoalAsInactive() {
        UUID userId = UUID.randomUUID();
        SavingGoal goal = goal(userId);
        when(repository.findById(goal.id())).thenReturn(Optional.of(goal));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        SavingGoal result = new DeactivateSavingGoalUseCase(repository).execute(goal.id(), userId);

        assertFalse(result.active());
        verify(repository).save(result);
    }

    @Test
    void deactivateThrowsWhenGoalDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> new DeactivateSavingGoalUseCase(repository).execute(id, UUID.randomUUID()));
        verify(repository, never()).save(any());
    }

    private SavingGoal goal(UUID owner) {
        return SavingGoal.builder().id(UUID.randomUUID()).idUser(owner).purpose("Viaje")
                .targetAmount(new BigDecimal("500")).currentAmount(BigDecimal.ZERO).active(true).build();
    }
}
