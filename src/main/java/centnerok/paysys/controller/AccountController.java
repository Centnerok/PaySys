package centnerok.paysys.controller;

import java.util.List;

import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import centnerok.paysys.model.dto.BalanceResponse;
import centnerok.paysys.model.dto.DepositRequest;
import centnerok.paysys.model.dto.TransactionResponse;
import centnerok.paysys.model.dto.TransferRequest;
import centnerok.paysys.service.AccountService;
import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest request) {
        return ResponseEntity
            .status(HttpStatus.OK)
                .body(service.makeTransfer(request));
    }
    
    @PostMapping("/{accountId}deposit")
    public ResponseEntity<TransactionResponse> deposit(@PathVariable @Positive Long accountId, @Valid @RequestBody DepositRequest request) {
        return ResponseEntity
            .status(HttpStatus.OK)
                .body(service.makeDeposit(accountId, request));
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable @Positive Long accountId) {
        return ResponseEntity
            .status(HttpStatus.OK)
                .body(service.getBalance(accountId));
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(@PathVariable @Positive Long accountId) {
        return ResponseEntity
            .status(HttpStatus.OK)
                .body(service.getTransactions(accountId));
    }
    
    
}
