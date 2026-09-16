package centnerok.paysys.handler;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import centnerok.paysys.exception.EmailAlreadyExistsException;
import centnerok.paysys.exception.InsufficientFundsException;
import centnerok.paysys.exception.InvalidDepositException;
import centnerok.paysys.exception.InvalidTransferException;
import centnerok.paysys.exception.ResourceNotFoundException;
import centnerok.paysys.model.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice 
@Slf4j 
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFound(ResourceNotFoundException e) {
        log.warn("Resource not found: {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), Instant.now());
    }

    @ExceptionHandler(InsufficientFundsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInsufficientFunds(InsufficientFundsException e) {
        log.warn("Insufficient funds: {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), Instant.now());
    }

    @ExceptionHandler(InvalidTransferException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidTransfer(InvalidTransferException e) {
        log.warn("Invalid transfer: {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), Instant.now());
    }

    @ExceptionHandler(InvalidDepositException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidDeposit(InvalidDepositException e) {
        log.warn("Invalid deposit: {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), Instant.now());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleEmailAlreadyExists(EmailAlreadyExistsException e) {
        log.warn("Email already exists: {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), Instant.now());
    }
}
