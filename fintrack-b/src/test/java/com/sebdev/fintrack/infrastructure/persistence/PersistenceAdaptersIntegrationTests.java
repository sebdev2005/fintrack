package com.sebdev.fintrack.infrastructure.persistence;

import com.sebdev.fintrack.domain.models.Account;
import com.sebdev.fintrack.domain.models.Category;
import com.sebdev.fintrack.domain.models.Movement;
import com.sebdev.fintrack.domain.models.SavingGoal;
import com.sebdev.fintrack.domain.models.types.AccountType;
import com.sebdev.fintrack.domain.models.types.MovementType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Testcontainers(disabledWithoutDocker = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class PersistenceAdaptersIntegrationTests {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"));
    @Autowired AccountPersistenceAdapter accountAdapter;
    @Autowired CategoryPersistenceAdapter categoryAdapter;
    @Autowired MovementPersistenceAdapter movementAdapter;
    @Autowired SavingGoalPersistenceAdapter savingGoalAdapter;

    @Test
    void accountAdapterSavesListsAndFindsAccount() {
        UUID user = UUID.randomUUID();
        Account account = Account.builder().id(UUID.randomUUID()).idUser(user).name("Ahorros")
                .type(AccountType.SAVINGS).initialBalance(new BigDecimal("100.00")).active(true).createdAt(Instant.now()).build();

        Account saved = accountAdapter.save(account);

        assertEquals(account.id(), saved.id());
        assertEquals(account.id(), accountAdapter.findById(account.id()).orElseThrow().id());
        assertEquals(1, accountAdapter.accounts(user).size());
    }

    @Test
    void categoryAdapterPersistsCategoryAndListsSystemAndUserCategories() {
        UUID user = UUID.randomUUID();
        Category category = Category.builder().id(UUID.randomUUID()).idUser(user).name("Comida")
                .createdAt(Instant.now()).active(true).build();
        Category systemCategory = Category.builder().id(UUID.randomUUID()).name("General")
                .createdAt(Instant.now()).active(true).build();

        categoryAdapter.save(category);
        categoryAdapter.save(systemCategory);

        assertEquals(category.id(), categoryAdapter.findById(category.id()).orElseThrow().id());
        assertEquals(2, categoryAdapter.categories(user).size());
    }

    @Test
    void movementAdapterPersistsAndFindsByAccountAndUser() {
        UUID user = UUID.randomUUID();
        Account account = Account.builder().id(UUID.randomUUID()).idUser(user).name("Corriente")
                .type(AccountType.CHECKING).initialBalance(BigDecimal.ZERO).active(true).createdAt(Instant.now()).build();
        accountAdapter.save(account);
        Movement movement = Movement.builder().id(UUID.randomUUID()).idAccount(account.id())
                .type(MovementType.EXPENSE).amount(new BigDecimal("12.50")).occurredOn(LocalDate.now())
                .createdAt(Instant.now()).build();

        Movement saved = movementAdapter.save(movement);

        assertEquals(movement.id(), saved.id());
        assertEquals(movement.id(), movementAdapter.findById(movement.id()).orElseThrow().id());
        assertEquals(1, movementAdapter.findByAccount(account.id()).size());
        assertEquals(1, movementAdapter.findByUser(user).size());
        assertEquals(0, new BigDecimal("-12.50").compareTo(movementAdapter.netByAccount(account.id())));
    }

    @Test
    void movementAdapterAggregatesMonthlyTotalsAndCategoryExpenses() {
        UUID user = UUID.randomUUID();
        Account account = Account.builder().id(UUID.randomUUID()).idUser(user).name("Corriente")
                .type(AccountType.CHECKING).initialBalance(BigDecimal.ZERO).active(true).createdAt(Instant.now()).build();
        accountAdapter.save(account);
        UUID categoryId = UUID.randomUUID();
        categoryAdapter.save(Category.builder().id(categoryId).idUser(user).name("Food").active(true).createdAt(Instant.now()).build());
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate until = from.plusMonths(1);
        movementAdapter.save(Movement.builder().id(UUID.randomUUID()).idAccount(account.id()).type(MovementType.INCOME)
                .amount(new BigDecimal("100.00")).occurredOn(from).createdAt(Instant.now()).build());
        movementAdapter.save(Movement.builder().id(UUID.randomUUID()).idAccount(account.id()).idCategory(categoryId)
                .type(MovementType.EXPENSE).amount(new BigDecimal("25.00")).occurredOn(from).createdAt(Instant.now()).build());
        movementAdapter.save(Movement.builder().id(UUID.randomUUID()).idAccount(account.id()).type(MovementType.EXPENSE)
                .amount(new BigDecimal("5.00")).occurredOn(until).createdAt(Instant.now()).build());

        assertEquals(0, new BigDecimal("100.00").compareTo(movementAdapter.totalByType(user, MovementType.INCOME, from, until)));
        assertEquals(0, new BigDecimal("25.00").compareTo(movementAdapter.totalByType(user, MovementType.EXPENSE, from, until)));
        var categoryTotals = movementAdapter.expensesByCategory(user, from, until);
        assertEquals(1, categoryTotals.size());
        assertEquals(categoryId, categoryTotals.getFirst().idCategory());
        assertEquals(0, new BigDecimal("25.00").compareTo(categoryTotals.getFirst().total()));
    }

    @Test
    void savingGoalAdapterPersistsFindsAndListsActiveGoals() {
        UUID user = UUID.randomUUID();
        SavingGoal goal = SavingGoal.builder().id(UUID.randomUUID()).idUser(user).purpose("Viaje")
                .targetAmount(new BigDecimal("500.00")).currentAmount(new BigDecimal("50.00"))
                .periodicContribution(new BigDecimal("25.00")).active(true).createdAt(Instant.now()).build();

        SavingGoal saved = savingGoalAdapter.save(goal);

        assertEquals(goal.id(), saved.id());
        assertEquals(goal.id(), savingGoalAdapter.findById(goal.id()).orElseThrow().id());
        assertEquals(1, savingGoalAdapter.savingGoals(user).size());
    }
}
