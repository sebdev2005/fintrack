package com.sebdev.fintrack.application.results;

import java.math.BigDecimal;
import java.util.UUID;

public record CategoryTotal(UUID idCategory, BigDecimal total) {
}
