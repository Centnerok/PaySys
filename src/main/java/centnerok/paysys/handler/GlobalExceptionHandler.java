package centnerok.paysys.handler;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import centnerok.paysys.exception.ResourceNotFoundException;
import centnerok.paysys.model.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice 
@Slf4j 
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFound(ResourceNotFoundException e) {
        log.warn("Resource Not FoundException: {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), Instant.now());
    }
}
