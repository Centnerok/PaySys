package centnerok.paysys.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import centnerok.paysys.exception.EmailAlreadyExistsException;
import centnerok.paysys.mapper.UserMapper;
import centnerok.paysys.model.dto.UserCreateRequest;
import centnerok.paysys.model.dto.UserResponse;
import centnerok.paysys.model.entity.User;
import centnerok.paysys.repository.UserRepository;
import centnerok.paysys.service.UserService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j 
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(
        UserRepository userRepository, 
        PasswordEncoder passwordEncoder, 
        UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Transactional 
    @Override
    public UserResponse register(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email=" + request.email() + " already exists");
        }

        User user = new User();

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPassword(
            passwordEncoder.encode(request.password())
        );

        userRepository.save(user);

        log.info("User created with id={}", user.getId());

        return userMapper.mapUserToResponse(user);
    }

}
