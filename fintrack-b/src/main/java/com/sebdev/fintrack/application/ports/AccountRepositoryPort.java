package com.sebdev.fintrack.application.ports;

import com.sebdev.fintrack.domain.models.Account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepositoryPort {
    Account save(Account account);
    List<Account> accounts(UUID idUser);
    Optional<Account> findById(UUID idAccount);

}
