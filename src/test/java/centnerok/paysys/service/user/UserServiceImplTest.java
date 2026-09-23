package centnerok.paysys.service.user;

import centnerok.paysys.exception.EmailAlreadyExistsException;
import centnerok.paysys.mapper.UserMapper;
import centnerok.paysys.model.dto.UserCreateRequest;
import centnerok.paysys.model.dto.UserResponse;
import centnerok.paysys.model.entity.User;
import centnerok.paysys.repository.UserRepository;
import centnerok.paysys.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    private UserMapper userMapper = new UserMapper();

    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UserServiceImpl(
                userRepository,
                passwordEncoder,
                userMapper
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        UserCreateRequest request = new UserCreateRequest(
                "Steve",
                "Jobs",
                "test@gmail.com",
                "1234"
        );

        String expected = "Email=" + request.email() + " already exists";

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> service.register(request)
        );

        assertEquals(expected, exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldCallEncoderToHashPassword() {
        UserCreateRequest request = new UserCreateRequest(
                "Steve",
                "Jobs",
                "test@gmail.com",
                "1234"
        );

        when(passwordEncoder.encode(request.password())).thenReturn("hashed_password");

        service.register(request);

        verify(passwordEncoder).encode(request.password());
    }

    @Test
    void shouldRegisterUser() {
        UserCreateRequest request = new UserCreateRequest(
                "Steve",
                "Jobs",
                "test@gmail.com",
                "1234"
        );

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("hashed_password");

        service.register(request);

        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();

        assertEquals("Steve", savedUser.getFirstName());
        assertEquals("Jobs", savedUser.getLastName());
        assertEquals("test@gmail.com", savedUser.getEmail());
        assertEquals("hashed_password",savedUser.getPassword());
    }

    @Test
    void shouldReturnMappedUserResponseWhenRegisterUser() {
        UserCreateRequest request = new UserCreateRequest(
                "Steve",
                "Jobs",
                "test@gmail.com",
                "1234"
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("hashed_password");

        UserResponse response = service.register(request);

        assertEquals("Steve", response.firstName());
        assertEquals("Jobs", response.lastName());
        assertEquals("test@gmail.com", response.email());
    }
}
