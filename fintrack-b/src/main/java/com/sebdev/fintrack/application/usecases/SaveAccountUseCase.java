package com.sebdev.fintrack.application.usecases;

import com.sebdev.fintrack.application.ports.AccountRepositoryPort;
import com.sebdev.fintrack.domain.models.Account;
import com.sebdev.fintrack.domain.models.AccountType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class SaveAccountUseCase {

    public record Command(String name, AccountType type,
                          BigDecimal initialBalance, String color, String bank) {}

    private final AccountRepositoryPort port;

    public SaveAccountUseCase(AccountRepositoryPort port) {
        this.port = port;
    }

    public Account execute(Command command, UUID idUser) {
        Account account = Account.builder()
                .id(UUID.randomUUID())
                .idUser(idUser)
                .name(command.name())
                .type(command.type())
                .initialBalance(command.initialBalance())
                .color(command.color())
                .bank(command.bank())
                .active(true)
                .createdAt(Instant.now())
                .build();
        return port.save(account);
    }
}
