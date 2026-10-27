package com.sebdev.fintrack.application.usecases;

import com.sebdev.fintrack.application.ports.AccountRepositoryPort;
import com.sebdev.fintrack.domain.models.Account;

import java.util.List;
import java.util.UUID;

public class ListAccountsUseCase {

    private final AccountRepositoryPort port;

    public ListAccountsUseCase(AccountRepositoryPort port) {
        this.port = port;
    }

    public List<Account> execute(UUID idUser){
        return port.accounts(idUser);
    }
}
