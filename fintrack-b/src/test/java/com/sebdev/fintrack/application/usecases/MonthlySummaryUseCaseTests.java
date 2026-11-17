package com.sebdev.fintrack.application.usecases;

import com.sebdev.fintrack.application.ports.MovementRepositoryPort;
import com.sebdev.fintrack.application.results.CategoryTotal;
import com.sebdev.fintrack.application.results.MonthlySummary;
import com.sebdev.fintrack.domain.models.types.MovementType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MonthlySummaryUseCaseTests {
    @Mock MovementRepositoryPort repository;
    @InjectMocks GetMonthlySummaryUseCase useCase;

    @Test
    void sumsTheRequestedMonthAndReturnsCategoryBreakdown() {
        UUID userId = UUID.randomUUID();
        YearMonth month = YearMonth.of(2026, 2);
        LocalDate from = month.atDay(1);
        LocalDate until = month.plusMonths(1).atDay(1);
        List<CategoryTotal> categories = List.of(new CategoryTotal(UUID.randomUUID(), new BigDecimal("35.50")));
        when(repository.totalByType(userId, MovementType.INCOME, from, until)).thenReturn(new BigDecimal("100"));
        when(repository.totalByType(userId, MovementType.EXPENSE, from, until)).thenReturn(new BigDecimal("35.50"));
        when(repository.expensesByCategory(userId, from, until)).thenReturn(categories);

        MonthlySummary summary = useCase.execute(month, userId);

        assertEquals(month, summary.month());
        assertEquals(new BigDecimal("100"), summary.income());
        assertEquals(new BigDecimal("35.50"), summary.expense());
        assertEquals(new BigDecimal("64.50"), summary.balance());
        assertEquals(categories, summary.expensesByCategory());
        verify(repository).totalByType(userId, MovementType.INCOME, from, until);
        verify(repository).totalByType(userId, MovementType.EXPENSE, from, until);
        verify(repository).expensesByCategory(userId, from, until);
    }
}
