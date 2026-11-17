package com.sebdev.fintrack.application.usecases;

import com.sebdev.fintrack.application.ports.*;
import com.sebdev.fintrack.domain.exceptions.custom.*;
import com.sebdev.fintrack.domain.models.*;
import com.sebdev.fintrack.domain.models.types.AccountType;
import com.sebdev.fintrack.domain.models.types.MovementType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoreUseCasesTests {
    @Mock AccountRepositoryPort accounts;
    @Mock CategoryRepositoryPort categories;
    @Mock MovementRepositoryPort movements;
    @Mock SavingGoalRepositoryPort goals;

    @Test
    void accountSaveAndListUseRepository() {
        UUID user = UUID.randomUUID();
        when(accounts.save(any())).thenAnswer(call -> call.getArgument(0));
        Account saved = new SaveAccountUseCase(accounts).execute(
                new SaveAccountUseCase.Command("Nómina", AccountType.CHECKING, BigDecimal.TEN, "blue", "Banco"), user);
        assertEquals("Nómina", saved.name());
        assertEquals(user, saved.idUser());
        assertTrue(saved.active());
        List<Account> expected = List.of(saved);
        when(accounts.accounts(user)).thenReturn(expected);
        assertEquals(expected, new ListAccountsUseCase(accounts).execute(user));
    }

    @Test
    void categorySaveAndListUseRepository() {
        UUID user = UUID.randomUUID();
        when(categories.save(any())).thenAnswer(call -> call.getArgument(0));
        Category saved = new SaveCategoryUseCase(categories).execute("Mercado", user);
        assertTrue(saved.active());
        assertEquals(user, saved.idUser());
        when(categories.categories(user)).thenReturn(List.of(saved));
        assertEquals(List.of(saved), new ListCategoriesUseCase(categories).execute(user));
    }

    @Test
    void deactivationUseCasesPersistReturnedInactiveCopies() {
        UUID user = UUID.randomUUID();
        Account account = account(user);
        Category category = Category.builder().id(UUID.randomUUID()).idUser(user).name("Mercado").active(true).build();
        SavingGoal goal = goal(user);
        when(accounts.findById(account.id())).thenReturn(Optional.of(account));
        when(accounts.save(any())).thenAnswer(call -> call.getArgument(0));
        when(categories.findById(category.id())).thenReturn(Optional.of(category));
        when(categories.save(any())).thenAnswer(call -> call.getArgument(0));
        when(goals.findById(goal.id())).thenReturn(Optional.of(goal));
        when(goals.save(any())).thenAnswer(call -> call.getArgument(0));

        assertFalse(new DeactivateAccountUseCase(accounts).execute(account.id(), user).active());
        assertFalse(new DeactivateCategoryUseCase(categories).execute(category.id(), user).active());
        assertFalse(new DeactivateSavingGoalUseCase(goals).execute(goal.id(), user).active());
    }

    @Test
    void deactivatingMissingAccountOrCategoryFails() {
        UUID id = UUID.randomUUID();
        when(accounts.findById(id)).thenReturn(Optional.empty());
        when(categories.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> new DeactivateAccountUseCase(accounts).execute(id, UUID.randomUUID()));
        assertThrows(ResourceNotFoundException.class, () -> new DeactivateCategoryUseCase(categories).execute(id, UUID.randomUUID()));
    }

    @Test
    void listMovementsForUserDelegatesToRepository() {
        UUID user = UUID.randomUUID();
        List<Movement> expected = List.of(movement(UUID.randomUUID()));
        when(movements.findByUser(user)).thenReturn(expected);
        assertEquals(expected, new ListMovementForUserUseCase(movements).execute(user));
    }

    @Test
    void listMovementsForAccountRequiresOwnership() {
        UUID user = UUID.randomUUID();
        Account account = account(user);
        List<Movement> expected = List.of(movement(account.id()));
        when(accounts.findById(account.id())).thenReturn(Optional.of(account));
        when(movements.findByAccount(account.id())).thenReturn(expected);
        assertEquals(expected, new ListMovementForAccountUseCase(accounts, movements).movements(account.id(), user));
        assertThrows(ResourceNotFoundException.class,
                () -> new ListMovementForAccountUseCase(accounts, movements).movements(account.id(), UUID.randomUUID()));
    }

    @Test
    void netByAccountIncludesInitialBalanceAndRejectsOtherOwners() {
        UUID user = UUID.randomUUID();
        Account account = account(user);
        when(accounts.findById(account.id())).thenReturn(Optional.of(account));
        when(movements.netByAccount(account.id())).thenReturn(new BigDecimal("25.00"));
        assertEquals(new BigDecimal("125.00"), new NetByAccountUseCase(movements, accounts).execute(account.id(), user));
        assertThrows(InvalidAccountException.class,
                () -> new NetByAccountUseCase(movements, accounts).execute(account.id(), UUID.randomUUID()));
    }

    @Test
    void saveMovementChecksOwnedAccountAndPersistsValidMovement() {
        UUID user = UUID.randomUUID();
        Account account = account(user);
        when(accounts.findById(account.id())).thenReturn(Optional.of(account));
        when(movements.save(any())).thenAnswer(call -> call.getArgument(0));
        var command = new SaveMovementUseCase.Command(account.id(), null, null, MovementType.EXPENSE,
                new BigDecimal("10"), LocalDate.now(), "Lunch");

        Movement result = new SaveMovementUseCase(accounts, categories, movements, goals).execute(command, user);

        assertEquals(account.id(), result.idAccount());
        assertEquals(new BigDecimal("10"), result.amount());
        verify(movements).save(any());
    }

    @Test
    void saveMovementRejectsInactiveOrUnownedAccount() {
        UUID user = UUID.randomUUID();
        Account inactive = Account.builder().id(UUID.randomUUID()).idUser(user).name("Cuenta")
                .type(AccountType.CHECKING).initialBalance(BigDecimal.ZERO).active(false).build();
        when(accounts.findById(inactive.id())).thenReturn(Optional.of(inactive));
        var command = new SaveMovementUseCase.Command(inactive.id(), null, null, MovementType.EXPENSE,
                BigDecimal.ONE, LocalDate.now(), null);
        SaveMovementUseCase useCase = new SaveMovementUseCase(accounts, categories, movements, goals);
        assertThrows(InvalidMovementException.class, () -> useCase.execute(command, user));
        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(command, UUID.randomUUID()));
        verify(movements, never()).save(any());
    }

    private Account account(UUID owner) {
        return Account.builder().id(UUID.randomUUID()).idUser(owner).name("Cuenta")
                .type(AccountType.CHECKING).initialBalance(new BigDecimal("100"))
                .active(true).createdAt(Instant.now()).build();
    }

    private SavingGoal goal(UUID owner) {
        return SavingGoal.builder().id(UUID.randomUUID()).idUser(owner).purpose("Viaje")
                .targetAmount(new BigDecimal("500")).currentAmount(BigDecimal.ZERO).active(true).build();
    }

    private Movement movement(UUID accountId) {
        return Movement.builder().id(UUID.randomUUID()).idAccount(accountId).type(MovementType.EXPENSE)
                .amount(BigDecimal.ONE).occurredOn(LocalDate.now()).build();
    }
}
