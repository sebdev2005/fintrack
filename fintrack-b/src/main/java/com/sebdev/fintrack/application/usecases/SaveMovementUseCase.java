package com.sebdev.fintrack.application.usecases;

import com.sebdev.fintrack.application.ports.AccountRepositoryPort;
import com.sebdev.fintrack.application.ports.CategoryRepositoryPort;
import com.sebdev.fintrack.application.ports.MovementRepositoryPort;
import com.sebdev.fintrack.application.ports.SavingGoalRepositoryPort;
import com.sebdev.fintrack.domain.exceptions.custom.InvalidMovementException;
import com.sebdev.fintrack.domain.exceptions.custom.ResourceNotFoundException;
import com.sebdev.fintrack.domain.models.Account;
import com.sebdev.fintrack.domain.models.Category;
import com.sebdev.fintrack.domain.models.Movement;
import com.sebdev.fintrack.domain.models.SavingGoal;
import com.sebdev.fintrack.domain.models.types.MovementType;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Transactional
public class SaveMovementUseCase {

    public record Command(UUID idAccount, UUID idCategory, UUID idSavingGoal, MovementType type, BigDecimal amount,
                          LocalDate occurredOn, String note){}

    private final AccountRepositoryPort accountRepositoryPort;
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final MovementRepositoryPort movementRepositoryPort;
    private final SavingGoalRepositoryPort savingGoalRepositoryPort;

    public SaveMovementUseCase(AccountRepositoryPort accountRepositoryPort, CategoryRepositoryPort categoryRepositoryPort, MovementRepositoryPort movementRepositoryPort, SavingGoalRepositoryPort savingGoalRepositoryPort) {
        this.accountRepositoryPort = accountRepositoryPort;
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.movementRepositoryPort = movementRepositoryPort;
        this.savingGoalRepositoryPort = savingGoalRepositoryPort;
    }

    public Movement execute(Command command, UUID idUser) {

        Movement movement = Movement.builder()
                .id(UUID.randomUUID())
                .idAccount(command.idAccount)
                .idCategory(command.idCategory)
                .idSavingGoal(command.idSavingGoal)
                .type(command.type)
                .amount(command.amount)
                .occurredOn(command.occurredOn)
                .note(command.note)
                .createdAt(Instant.now())
                .build();

        Account account = accountRepositoryPort.findById(command.idAccount())
                .filter((a)-> a.isOwnedBy(idUser))
                .orElseThrow(()-> new ResourceNotFoundException("La cuenta no fue encontrada"));

        if(!account.active()){
            throw new InvalidMovementException("No se pueden registrar movimientos en una cuenta inactiva");
        }

        if(command.idCategory != null){
           categoryRepositoryPort.findById(command.idCategory())
                    .filter((c)-> c.isAvailableFor(idUser))
                    .orElseThrow(()-> new ResourceNotFoundException("La categoria no fue encontrada"));
        }

        if(command.idSavingGoal != null){
            if(command.type != MovementType.EXPENSE){
                throw new InvalidMovementException("Los aportes a los ahorros son gastos");
            }
            SavingGoal savingGoal = savingGoalRepositoryPort.findById(command.idSavingGoal())
                    .filter((s) -> s.isOwnedBy(idUser))
                    .orElseThrow(()-> new ResourceNotFoundException("La meta de ahorro no fue encontrada"));
            savingGoalRepositoryPort.save(savingGoal.contribute(command.amount));
        }
        return movementRepositoryPort.save(movement);
    }


}
