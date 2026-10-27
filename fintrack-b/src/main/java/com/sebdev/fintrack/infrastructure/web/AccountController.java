package com.sebdev.fintrack.infrastructure.web;


import com.sebdev.fintrack.application.usecases.DeactivateAccountUseCase;
import com.sebdev.fintrack.application.usecases.ListAccountsUseCase;
import com.sebdev.fintrack.application.usecases.SaveAccountUseCase;
import com.sebdev.fintrack.domain.models.Account;
import com.sebdev.fintrack.domain.models.types.AccountType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final SaveAccountUseCase saveAccountUseCase;
    private final ListAccountsUseCase listAccountsUseCase;
    private final DeactivateAccountUseCase deactivateAccountUseCase;

    public AccountController(SaveAccountUseCase saveAccountUseCase, ListAccountsUseCase listAccountsUseCase, DeactivateAccountUseCase deactivateAccountUseCase) {
        this.saveAccountUseCase = saveAccountUseCase;
        this.listAccountsUseCase = listAccountsUseCase;
        this.deactivateAccountUseCase = deactivateAccountUseCase;
    }
    @PostMapping("/createAccount")
    public AccountResponse save(@Valid @RequestBody AccountRequest request, @AuthenticationPrincipal Jwt jwt){
        UUID idUser =  UUID.fromString(jwt.getSubject());
        Account account = saveAccountUseCase.execute( new SaveAccountUseCase.Command(request.name, request.type, request.initialBalance, request.color, request.bank), idUser);
        return AccountResponse.from(account);
    }

    @GetMapping("/accounts")
    public List<AccountResponse> accounts(@AuthenticationPrincipal Jwt jwt){
        UUID idUser =  UUID.fromString(jwt.getSubject());
        return listAccountsUseCase.execute(idUser).stream()
                .map(AccountResponse::from).toList();

    }

    @PatchMapping("/{idAccount}/deactivate")
    public AccountResponse deactivate(@PathVariable UUID idAccount, @AuthenticationPrincipal Jwt jwt){
        UUID idUser =  UUID.fromString(jwt.getSubject());
        Account account = deactivateAccountUseCase.execute(idAccount, idUser);
        return AccountResponse.from(account);
    }


    public record AccountRequest(
            @NotBlank @Size(max = 40)String name, @NotNull AccountType type, @NotNull BigDecimal initialBalance,
            String color, String bank
    ){

    }

    public record AccountResponse(UUID id, UUID idUser, String name, AccountType type, BigDecimal initialBalance,
                                  String color, String bank, boolean active, Instant createdAt){
        private static AccountResponse from(Account account){
            return new AccountResponse(
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
}
