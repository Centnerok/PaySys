package centnerok.paysys.service;

import centnerok.paysys.model.dto.UserCreateRequest;
import centnerok.paysys.model.dto.UserResponse;

public interface UserService {
    UserResponse register(UserCreateRequest request);
}
