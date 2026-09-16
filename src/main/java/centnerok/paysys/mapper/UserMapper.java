package centnerok.paysys.mapper;

import org.springframework.stereotype.Component;

import centnerok.paysys.model.dto.UserResponse;
import centnerok.paysys.model.entity.User;

@Component 
public class UserMapper {
    public UserResponse mapUserToResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getCreatedAt()
        );
    }
}
