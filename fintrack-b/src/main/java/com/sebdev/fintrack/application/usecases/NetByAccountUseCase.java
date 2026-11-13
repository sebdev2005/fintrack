package com.sebdev.fintrack.application.usecases;

import com.sebdev.fintrack.application.ports.AccountRepositoryPort;
import com.sebdev.fintrack.application.ports.MovementRepositoryPort;
import com.sebdev.fintrack.domain.exceptions.custom.InvalidAccountException;
import com.sebdev.fintrack.domain.models.Account;

import java.lang.reflect.AccessFlag;
import java.math.BigDecimal;
import java.util.UUID;

public class NetByAccountUseCase {

    private final MovementRepositoryPort movementRepositoryPort;
    private final AccountRepositoryPort accountRepositoryPort;

    public NetByAccountUseCase(MovementRepositoryPort movementRepositoryPort, AccountRepositoryPort accountRepositoryPort) {
        this.movementRepositoryPort = movementRepositoryPort;
        this.accountRepositoryPort = accountRepositoryPort;
    }

    public BigDecimal execute(UUID idAccount, UUID idUser){
        Account account = accountRepositoryPort.findById(idAccount)
                .filter(a -> a.isOwnedBy(idUser)).
                orElseThrow(()-> new InvalidAccountException("Cuenta no encontrada"));

        BigDecimal newAmount = movementRepositoryPort.netByAccount(idAccount);

        return account.initialBalance().add(newAmount);
    }
}
