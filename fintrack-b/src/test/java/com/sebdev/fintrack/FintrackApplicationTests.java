package com.sebdev.fintrack;

import com.sebdev.fintrack.domain.exceptions.custom.InvalidAccountException;
import com.sebdev.fintrack.domain.models.Account;
import com.sebdev.fintrack.domain.models.types.AccountType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class FintrackApplicationTests {


		private static final UUID OWNER = UUID.randomUUID();

		// Cuenta válida por defecto: cada test cambia solo lo que quiere probar
		private Account.AccountBuilder validAccount() {
			return Account.builder()
					.id(UUID.randomUUID())
					.idUser(OWNER)
					.name("Cuenta de ahorro")
					.type(AccountType.SAVINGS)
					.initialBalance(BigDecimal.ZERO)
					.bank("NU")
					.active(true)
					.createdAt(Instant.now());
		}

		@Test
		void createsAccountWithValidData() {
			Account account = validAccount().build();

			assertEquals("Cuenta de ahorro", account.getName());
			assertTrue(account.isActive());
		}

		@ParameterizedTest
		@ValueSource(strings = {"", "   "})
		void rejectsBlankName(String name) {
			assertThrows(InvalidAccountException.class,
					() -> validAccount().name(name).build());
		}

		@Test
		void rejectsNameLongerThan40() {
			assertThrows(InvalidAccountException.class,
					() -> validAccount().name("a".repeat(41)).build());
		}

		@Test
		void rejectsNullUser() {
			assertThrows(InvalidAccountException.class,
					() -> validAccount().idUser(null).build());
		}
	}