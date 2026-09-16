package centnerok.paysys.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import centnerok.paysys.model.dto.AccountResponse;
import centnerok.paysys.model.dto.UserCreateRequest;
import centnerok.paysys.model.dto.UserResponse;
import centnerok.paysys.service.AccountService;
import centnerok.paysys.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;


@RestController 
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final AccountService accountService;

    public UserController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
                .body(userService.register(request));
    }

    @PostMapping("/{userId}/accounts")
    public ResponseEntity<AccountResponse> createAccount(@PathVariable @Positive  Long userId) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
                .body(accountService.createAccount(userId));
    }
    
}
