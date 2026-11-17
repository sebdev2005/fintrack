package com.sebdev.fintrack.domain.exceptions;

import com.sebdev.fintrack.domain.exceptions.custom.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalHandlerExceptionsTests {
    private final GlobalHandlerExceptions handler = new GlobalHandlerExceptions();

    @Test
    void malformedJsonAndEnumMapToBadRequest() {
        var response = handler.handlerUnreadableMessage(new HttpMessageNotReadableException("bad json", null));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().status());
    }

    @Test
    void argumentTypeMismatchIncludesParameterName() {
        var exception = new MethodArgumentTypeMismatchException("not-a-uuid", java.util.UUID.class,
                "id", null, new IllegalArgumentException());
        var response = handler.handlerArgumentTypeMismatch(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(true, response.getBody().message().contains("id"));
    }

    @Test
    void missingParameterNamesTheMissingArgument() {
        var response = handler.handlerMissingRequestParameter(new MissingServletRequestParameterException("month", "YearMonth"));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(true, response.getBody().message().contains("month"));
    }

    @Test
    void domainExceptionsMapToTheirExpectedStatuses() {
        assertEquals(400, handler.handlerFieldVoid(new FieldVoidException("required")).getStatusCode().value());
        assertEquals(400, handler.handlerInvalidCategory(new InvalidCategoryException("bad category")).getStatusCode().value());
        assertEquals(400, handler.handlerInvalidAccount(new InvalidAccountException("bad account")).getStatusCode().value());
        assertEquals(400, handler.handlerInvalidMovement(new InvalidMovementException("bad movement")).getStatusCode().value());
        assertEquals(400, handler.handlerInvalidSavingGoal(new InvalidSavingGoalException("bad goal")).getStatusCode().value());
        assertEquals(403, handler.handlerCategoryOwnership(new CategoryOwnershipException("forbidden")).getStatusCode().value());
        assertEquals(403, handler.handlerAccountOwnership(new AccountOwnershipException("forbidden")).getStatusCode().value());
        assertEquals(403, handler.handlerSavingGoalOwnership(new SavingGoalOwnershipException("forbidden")).getStatusCode().value());
        assertEquals(404, handler.handlerNotFound(new ResourceNotFoundException("missing")).getStatusCode().value());
    }
}
