package com.sebdev.fintrack.infrastructure.web;


import com.sebdev.fintrack.application.results.MonthlySummary;
import com.sebdev.fintrack.application.usecases.GetMonthlySummaryUseCase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/summary")
public class SummaryController {

    private final GetMonthlySummaryUseCase getMonthlySummaryUseCase;

    public SummaryController(GetMonthlySummaryUseCase getMonthlySummaryUseCase) {
        this.getMonthlySummaryUseCase = getMonthlySummaryUseCase;
    }


    @GetMapping
    public SummaryResponse getSummaryMonthly(@RequestParam("month") @DateTimeFormat(pattern = "yyyy-MM") YearMonth month, @AuthenticationPrincipal Jwt jwt){
        UUID idUser = UUID.fromString(jwt.getSubject());
        return SummaryResponse.from(getMonthlySummaryUseCase.execute(month, idUser));

    }

    public record SummaryResponse(String month,
                                  BigDecimal income,
                                  BigDecimal expenses,
                                  BigDecimal balance,
                                  List<CategoryTotalResponse> expensesByCategory){
        private static SummaryResponse from(MonthlySummary summary){
            return new SummaryResponse(
                    summary.month().toString(),
                    summary.income(),
                    summary.expense(),
                    summary.balance(),
                    summary.expensesByCategory().stream()
                            .map(c -> new CategoryTotalResponse(c.idCategory(), c.total()))
                            .toList());

        }

    }
    public record CategoryTotalResponse(UUID idCategory, BigDecimal total){

    }
}

