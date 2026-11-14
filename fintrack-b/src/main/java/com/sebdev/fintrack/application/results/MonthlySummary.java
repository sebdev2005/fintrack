package com.sebdev.fintrack.application.results;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public record MonthlySummary(YearMonth month, BigDecimal income, BigDecimal expense, List<CategoryTotal> expensesByCategory) {

    public BigDecimal balance(){
        return income.subtract(expense);
    }

}
