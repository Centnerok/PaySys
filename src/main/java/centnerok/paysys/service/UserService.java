package centnerok.paysys.service;

import centnerok.paysys.model.dto.AccountResponse;
import centnerok.paysys.model.dto.UserCreateRequest;
import centnerok.paysys.model.dto.UserResponse;

public interface UserService {
    AccountResponse createAccount(Long userId);

    UserResponse register(UserCreateRequest request);
}
