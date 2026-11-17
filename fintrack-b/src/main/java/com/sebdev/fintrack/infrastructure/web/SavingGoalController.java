package com.sebdev.fintrack.infrastructure.web;

import com.sebdev.fintrack.application.usecases.DeactivateSavingGoalUseCase;
import com.sebdev.fintrack.application.usecases.ListSavingGoalsUseCase;
import com.sebdev.fintrack.application.usecases.SaveSavingGoalUseCase;
import com.sebdev.fintrack.domain.models.SavingGoal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/saving-goals")
public class SavingGoalController {

    private final SaveSavingGoalUseCase saveSavingGoalUseCase;
    private final ListSavingGoalsUseCase listSavingGoalsUseCase;
    private final DeactivateSavingGoalUseCase deactivateSavingGoalUseCase;

    public SavingGoalController(SaveSavingGoalUseCase saveSavingGoalUseCase, ListSavingGoalsUseCase listSavingGoalsUseCase, DeactivateSavingGoalUseCase deactivateSavingGoalUseCase) {
        this.saveSavingGoalUseCase = saveSavingGoalUseCase;
        this.listSavingGoalsUseCase = listSavingGoalsUseCase;
        this.deactivateSavingGoalUseCase = deactivateSavingGoalUseCase;
    }
    @PostMapping("/save")
    public SavingGoalResponse save(@Valid @RequestBody SavingGoalRequest request, @AuthenticationPrincipal Jwt jwt){
        UUID idUser = UUID.fromString(Objects.requireNonNull(jwt.getSubject()));
        return SavingGoalResponse.from(
                saveSavingGoalUseCase.execute
                        (new SaveSavingGoalUseCase.Command(request.purpose, request.targetAmount, request.deadline, request.periodicContribution),idUser));
    }

    @GetMapping
    public List<SavingGoalResponse> savingGoals(@AuthenticationPrincipal Jwt jwt){
        UUID idUser = UUID.fromString(Objects.requireNonNull(jwt.getSubject()));
        return listSavingGoalsUseCase.execute(idUser)
                .stream().map(SavingGoalResponse::from)
                .toList();
    }

    @PatchMapping("/{idSavingGoal}/deactivate")
    public SavingGoalResponse deactivate(@PathVariable UUID idSavingGoal, @AuthenticationPrincipal Jwt jwt){
        UUID idUser = UUID.fromString(Objects.requireNonNull(jwt.getSubject()));
        return SavingGoalResponse.from(deactivateSavingGoalUseCase.execute(idSavingGoal, idUser));

    }

    public record SavingGoalRequest(@NotBlank @Size(max = 60) String purpose, @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal targetAmount, LocalDate deadline, BigDecimal periodicContribution){

    }

    public record SavingGoalResponse(UUID id, UUID idUser, String purpose, BigDecimal targetAmount, BigDecimal currentAmount, LocalDate deadline, BigDecimal periodicContribution, boolean active, Instant createdAt){

        public static SavingGoalResponse from(SavingGoal savingGoal){
            return new SavingGoalResponse(
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

    }
}
