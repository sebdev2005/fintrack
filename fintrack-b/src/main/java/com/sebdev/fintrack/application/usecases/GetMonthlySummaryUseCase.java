package com.sebdev.fintrack.application.usecases;

import com.sebdev.fintrack.application.ports.AccountRepositoryPort;
import com.sebdev.fintrack.application.ports.MovementRepositoryPort;
import com.sebdev.fintrack.application.results.CategoryTotal;
import com.sebdev.fintrack.application.results.MonthlySummary;
import com.sebdev.fintrack.domain.models.Account;
import com.sebdev.fintrack.domain.models.Movement;
import com.sebdev.fintrack.domain.models.types.MovementType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public class GetMonthlySummaryUseCase {

    private final MovementRepositoryPort movementRepositoryPort;

    public GetMonthlySummaryUseCase(MovementRepositoryPort movementRepositoryPort) {
        this.movementRepositoryPort = movementRepositoryPort;

    }

    public MonthlySummary execute(YearMonth month, UUID idUser){
        LocalDate start = month.atDay(1);
        LocalDate end = month.plusMonths(1).atDay(1);
        BigDecimal income = movementRepositoryPort.totalByType(idUser, MovementType.INCOME, start, end);
        BigDecimal expenses = movementRepositoryPort.totalByType(idUser, MovementType.EXPENSE, start, end);
        List<CategoryTotal> totalExpensesByCategory = movementRepositoryPort.expensesByCategory(idUser, start, end);

        return new MonthlySummary(month, income, expenses, totalExpensesByCategory);
    }
}
