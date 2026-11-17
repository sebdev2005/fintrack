package com.sebdev.fintrack.domain.models;

import com.sebdev.fintrack.domain.exceptions.custom.*;
import com.sebdev.fintrack.domain.models.types.AccountType;
import com.sebdev.fintrack.domain.models.types.MovementType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DomainModelsTests {
    @Test void accountDeactivationRequiresOwnerAndReturnsInactiveCopy() {
        UUID owner = UUID.randomUUID();
        Account account = Account.builder().id(UUID.randomUUID()).idUser(owner).name("Main").type(AccountType.CHECKING)
                .initialBalance(BigDecimal.ZERO).active(true).build();
        Account deactivated = account.deactivate(owner);
        assertFalse(deactivated.active());
        assertTrue(account.active());
        assertTrue(account.isOwnedBy(owner));
        assertSame(deactivated, deactivated.deactivate(owner));
        assertThrows(InvalidAccountException.class, () -> account.deactivate(null));
        assertThrows(AccountOwnershipException.class, () -> account.deactivate(UUID.randomUUID()));
    }

    @Test void categoryAvailabilityAndDeactivationRespectOwnerAndSystemCategories() {
        UUID owner = UUID.randomUUID();
        Category own = Category.builder().id(UUID.randomUUID()).idUser(owner).name("Food").active(true).build();
        Category system = Category.builder().id(UUID.randomUUID()).name("General").active(true).build();
        assertTrue(own.isAvailableFor(owner));
        assertFalse(own.isAvailableFor(UUID.randomUUID()));
        assertTrue(system.isAvailableFor(UUID.randomUUID()));
        assertFalse(own.deactivated(owner).active());
        assertThrows(InvalidCategoryException.class, () -> own.deactivated(null));
        assertThrows(CategoryOwnershipException.class, () -> own.deactivated(UUID.randomUUID()));
        assertThrows(InvalidCategoryException.class, () -> Category.builder().name(" ").build());
    }

    @Test void movementRejectsInvalidAmountsDatesAndLongNotes() {
        var builder = Movement.builder().idAccount(UUID.randomUUID()).type(MovementType.EXPENSE)
                .amount(BigDecimal.ONE).occurredOn(LocalDate.now());
        assertNotNull(builder.build());
        assertThrows(InvalidMovementException.class, () -> Movement.builder().idAccount(UUID.randomUUID())
                .type(MovementType.EXPENSE).amount(BigDecimal.ZERO).occurredOn(LocalDate.now()).build());
        assertThrows(InvalidMovementException.class, () -> Movement.builder().idAccount(UUID.randomUUID())
                .type(MovementType.EXPENSE).amount(BigDecimal.ONE).occurredOn(LocalDate.now()).note("x".repeat(201)).build());
    }

    @Test void savingGoalValidatesAmountsOwnershipAndContributions() {
        UUID owner = UUID.randomUUID();
        SavingGoal goal = SavingGoal.builder().id(UUID.randomUUID()).idUser(owner).purpose("Trip")
                .targetAmount(BigDecimal.TEN).currentAmount(BigDecimal.ONE).active(true).build();
        assertEquals(0, new BigDecimal("4").compareTo(goal.contribute(new BigDecimal("3")).currentAmount()));
        assertTrue(goal.isOwnedBy(owner));
        assertFalse(goal.deactivate(owner).active());
        assertThrows(InvalidSavingGoalException.class, () -> goal.contribute(BigDecimal.ZERO));
        assertThrows(InvalidSavingGoalException.class, () -> goal.deactivate(null));
        assertThrows(SavingGoalOwnershipException.class, () -> goal.deactivate(UUID.randomUUID()));
        assertThrows(InvalidSavingGoalException.class, () -> SavingGoal.builder().idUser(owner).purpose(null)
                .targetAmount(BigDecimal.TEN).currentAmount(BigDecimal.ZERO).build());
    }
}
