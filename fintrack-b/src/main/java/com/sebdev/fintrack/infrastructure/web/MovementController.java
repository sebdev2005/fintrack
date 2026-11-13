package com.sebdev.fintrack.infrastructure.web;

import com.nimbusds.jose.shaded.jcip.NotThreadSafe;
import com.sebdev.fintrack.application.usecases.ListMovementForAccountUseCase;
import com.sebdev.fintrack.application.usecases.ListMovementForUserUseCase;
import com.sebdev.fintrack.application.usecases.SaveMovementUseCase;
import com.sebdev.fintrack.domain.models.Movement;
import com.sebdev.fintrack.domain.models.types.MovementType;
import com.sebdev.fintrack.infrastructure.persistence.MovementEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
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
@RequestMapping("/api/movements")
public class MovementController {

    private final SaveMovementUseCase saveMovementUseCase;
    private final ListMovementForUserUseCase listMovementForUserUseCase;
    private final ListMovementForAccountUseCase listMovementForAccountUseCase;

    public MovementController(SaveMovementUseCase saveMovementUseCase, ListMovementForUserUseCase listMovementForUserUseCase, ListMovementForAccountUseCase listMovementForAccountUseCase) {
        this.saveMovementUseCase = saveMovementUseCase;
        this.listMovementForUserUseCase = listMovementForUserUseCase;
        this.listMovementForAccountUseCase = listMovementForAccountUseCase;
    }

    @PostMapping("/createMovement")
    public MovementResponse save(@Valid @RequestBody MovementRequest request, @AuthenticationPrincipal Jwt jwt){
        UUID idUser = UUID.fromString(Objects.requireNonNull(jwt.getSubject()));
        Movement movement = saveMovementUseCase.execute(new SaveMovementUseCase.Command(request.idAccount,request.idCategory,request.type,request.amount, request.occurredOn,request.note), idUser);
        return MovementResponse.from(movement);
    }

    @GetMapping("/forAccounts")
    public List<MovementResponse> movementsForAccount(@RequestParam UUID idAccount, @AuthenticationPrincipal Jwt jwt){
        UUID idUser = UUID.fromString(Objects.requireNonNull(jwt.getSubject()));
        return listMovementForAccountUseCase.movements(idAccount, idUser)
                .stream().map(MovementResponse::from)
                .toList();
    }

    @GetMapping("/all")
    public List<MovementResponse> movementsForUser(@AuthenticationPrincipal Jwt jwt){
        UUID idUser = UUID.fromString(Objects.requireNonNull(jwt.getSubject()));
        return listMovementForUserUseCase.execute( idUser)
                .stream().map(MovementResponse::from)
                .toList();
    }

    public record MovementRequest(@NotNull UUID idAccount, UUID idCategory, @NotNull MovementType type, @DecimalMin(value = "0.0") BigDecimal amount,
                                   @NotNull LocalDate occurredOn, @Size(max = 200) String note){

    }

    public record MovementResponse(UUID id, UUID idAccount, UUID idCategory, MovementType type, BigDecimal amount,
                                    LocalDate occurredOn, String note, Instant createdAt){
        private static MovementResponse from(Movement movement){
            return new MovementResponse(
                    movement.id(),
                    movement.idAccount(),
                    movement.idCategory(),
                    movement.type(),
                    movement.amount(),
                    movement.occurredOn(),
                    movement.note(),
                    movement.createdAt()
            );
        }

    }
}
