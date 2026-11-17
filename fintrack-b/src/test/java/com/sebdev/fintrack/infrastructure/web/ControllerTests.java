package com.sebdev.fintrack.infrastructure.web;

import com.sebdev.fintrack.application.results.CategoryTotal;
import com.sebdev.fintrack.application.results.MonthlySummary;
import com.sebdev.fintrack.application.usecases.*;
import com.sebdev.fintrack.domain.models.*;
import com.sebdev.fintrack.domain.models.types.AccountType;
import com.sebdev.fintrack.domain.models.types.MovementType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControllerTests {
    private final UUID userId = UUID.randomUUID();
    private Jwt jwt() { return Jwt.withTokenValue("token").header("alg", "none").subject(userId.toString()).build(); }

    @Mock SaveAccountUseCase saveAccount;
    @Mock ListAccountsUseCase listAccounts;
    @Mock DeactivateAccountUseCase deactivateAccount;
    @Test void accountControllerDelegatesSaveListAndDeactivate() {
        var controller = new AccountController(saveAccount, listAccounts, deactivateAccount);
        Account account = Account.builder().id(UUID.randomUUID()).idUser(userId).name("Main").type(AccountType.CHECKING).initialBalance(BigDecimal.TEN).active(true).build();
        when(saveAccount.execute(any(), eq(userId))).thenReturn(account);
        when(listAccounts.execute(userId)).thenReturn(List.of(account));
        when(deactivateAccount.execute(account.id(), userId)).thenReturn(account);
        assertEquals(account.id(), controller.save(new AccountController.AccountRequest("Main", AccountType.CHECKING, BigDecimal.TEN, null, null), jwt()).id());
        assertEquals(1, controller.accounts(jwt()).size());
        assertEquals(account.id(), controller.deactivate(account.id(), jwt()).id());
        verify(saveAccount).execute(any(), eq(userId));
    }

    @Mock SaveCategoryUseCase saveCategory;
    @Mock ListCategoriesUseCase listCategories;
    @Mock DeactivateCategoryUseCase deactivateCategory;
    @Test void categoryControllerDelegatesSaveListAndDeactivate() {
        var controller = new CategoryController(saveCategory, listCategories, deactivateCategory);
        Category category = Category.builder().id(UUID.randomUUID()).idUser(userId).name("Food").active(true).build();
        when(saveCategory.execute("Food", userId)).thenReturn(category);
        when(listCategories.execute(userId)).thenReturn(List.of(category));
        when(deactivateCategory.execute(category.id(), userId)).thenReturn(category);
        assertEquals(category.id(), controller.save(new CategoryController.SaveCategoryRequest("Food"), jwt()).id());
        assertEquals(1, controller.list(jwt()).size());
        assertEquals(category.id(), controller.deactivate(category.id(), jwt()).id());
    }

    @Mock SaveMovementUseCase saveMovement;
    @Mock ListMovementForUserUseCase listMovementsForUser;
    @Mock ListMovementForAccountUseCase listMovementsForAccount;
    @Mock NetByAccountUseCase netByAccount;
    @Test void movementControllerDelegatesCreateListsAndBalance() {
        var controller = new MovementController(saveMovement, listMovementsForUser, listMovementsForAccount, netByAccount);
        UUID accountId = UUID.randomUUID();
        Movement movement = movement(accountId);
        when(saveMovement.execute(any(), eq(userId))).thenReturn(movement);
        when(listMovementsForUser.execute(userId)).thenReturn(List.of(movement));
        when(listMovementsForAccount.movements(accountId, userId)).thenReturn(List.of(movement));
        when(netByAccount.execute(accountId, userId)).thenReturn(new BigDecimal("10"));
        var request = new MovementController.MovementRequest(accountId, null, null, MovementType.EXPENSE, BigDecimal.ONE, LocalDate.now(), null);
        assertEquals(movement.id(), controller.save(request, jwt()).id());
        assertEquals(1, controller.movementsForUser(jwt()).size());
        assertEquals(1, controller.movementsForAccount(accountId, jwt()).size());
        assertEquals(new BigDecimal("10"), controller.balanceForAccount(accountId, jwt()).balance());
    }

    @Mock SaveSavingGoalUseCase saveGoal;
    @Mock ListSavingGoalsUseCase listGoals;
    @Mock DeactivateSavingGoalUseCase deactivateGoal;
    @Test void savingGoalControllerDelegatesCreateListAndDeactivate() {
        var controller = new SavingGoalController(saveGoal, listGoals, deactivateGoal);
        SavingGoal goal = goal(userId);
        when(saveGoal.execute(any(), eq(userId))).thenReturn(goal);
        when(listGoals.execute(userId)).thenReturn(List.of(goal));
        when(deactivateGoal.execute(goal.id(), userId)).thenReturn(goal);
        var request = new SavingGoalController.SavingGoalRequest("Trip", BigDecimal.TEN, null, null);
        assertEquals(goal.id(), controller.save(request, jwt()).id());
        assertEquals(1, controller.savingGoals(jwt()).size());
        assertEquals(goal.id(), controller.deactivate(goal.id(), jwt()).id());
    }

    @Mock GetMonthlySummaryUseCase monthlySummary;
    @Test void summaryControllerMapsMonthlySummaryToResponse() {
        var controller = new SummaryController(monthlySummary);
        YearMonth month = YearMonth.of(2026, 1);
        UUID categoryId = UUID.randomUUID();
        when(monthlySummary.execute(month, userId)).thenReturn(new MonthlySummary(month, new BigDecimal("50"),
                new BigDecimal("20"), List.of(new CategoryTotal(categoryId, new BigDecimal("20")))));
        var response = controller.getSummaryMonthly(month, jwt());
        assertEquals("2026-01", response.month());
        assertEquals(new BigDecimal("30"), response.balance());
        assertEquals(categoryId, response.expensesByCategory().getFirst().idCategory());
    }

    private Movement movement(UUID accountId) {
        return Movement.builder().id(UUID.randomUUID()).idAccount(accountId).type(MovementType.EXPENSE)
                .amount(BigDecimal.ONE).occurredOn(LocalDate.now()).createdAt(Instant.now()).build();
    }

    private SavingGoal goal(UUID owner) {
        return SavingGoal.builder().id(UUID.randomUUID()).idUser(owner).purpose("Trip").targetAmount(BigDecimal.TEN)
                .currentAmount(BigDecimal.ZERO).active(true).createdAt(Instant.now()).build();
    }
}
