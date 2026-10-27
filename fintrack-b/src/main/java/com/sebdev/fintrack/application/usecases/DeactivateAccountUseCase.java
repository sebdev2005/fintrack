package com.sebdev.fintrack.application.usecases;

import com.sebdev.fintrack.application.ports.AccountRepositoryPort;
import com.sebdev.fintrack.domain.exceptions.custom.ResourceNotFoundException;
import com.sebdev.fintrack.domain.models.Account;

import java.util.UUID;

public class DeactivateAccountUseCase {

    private final AccountRepositoryPort port;

    public DeactivateAccountUseCase(AccountRepositoryPort port) {
        this.port = port;
    }

    public Account execute(UUID id, UUID idUser){
        Account account = port.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro la cuenta"));
        account.deactivate(idUser);
        return port.save(account);
    }
}
