package com.sebdev.fintrack.infrastructure.persistence;

import com.sebdev.fintrack.application.ports.AccountRepositoryPort;
import com.sebdev.fintrack.domain.models.Account;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AccountPersistenceAdapter implements AccountRepositoryPort {

    private final AccountJpaRepository repository;

    public AccountPersistenceAdapter(AccountJpaRepository repository){
        this.repository = repository;
    }

    @Override
    public Account save(Account account) {
        AccountEntity accountEntity = repository.save(toEntity(account));
        return toDomain(accountEntity);
    }

    @Override
    public List<Account> accounts(UUID idUser) {
        return repository.accounts(idUser)
                .stream().map(AccountPersistenceAdapter::toDomain)
                .toList();
    }

    @Override
    public Optional<Account> findById(UUID idAccount) {
        return repository.findById(idAccount)
                .map(AccountPersistenceAdapter::toDomain);
    }

    private static Account toDomain(AccountEntity accountEntity){
        return Account.builder()
                .id(accountEntity.getId())
                .idUser(accountEntity.getIdUser())
                .name(accountEntity.getName())
                .type(accountEntity.getType())
                .initialBalance(accountEntity.getInitialBalance())
                .color(accountEntity.getColor())
                .bank(accountEntity.getBank())
                .active(accountEntity.isActive())
                .createdAt(accountEntity.getCreatedAt()).build();
    }
    private static AccountEntity toEntity(Account account){
        return new AccountEntity(
                account.getId(),
                account.getIdUser(),
                account.getName(),
                account.getType(),
                account.getInitialBalance(),
                account.getColor(),
                account.getBank(),
                account.isActive(),
                account.getCreatedAt()
        );
    }
}
