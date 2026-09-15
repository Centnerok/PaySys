package centnerok.paysys.handler;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
        log.warn("Resource Not Found Exception: {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), Instant.now());
    }

    @ExceptionHandler(InsufficientFundsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInsufficientFunds(InsufficientFundsException e) {
        log.warn("Insufficient Funds Exception: {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), Instant.now());
    }

    @ExceptionHandler(InvalidTransferException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInsufficientFunds(InvalidTransferException e) {
        log.warn("Invalid Transfer Exception: {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), Instant.now());
    }

    @ExceptionHandler(InvalidDepositException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse InvalidDepositException(InvalidTransferException e) {
        log.warn("Invalid Deposit Exception: {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), Instant.now());
    }
}
