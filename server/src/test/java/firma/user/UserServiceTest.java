package firma.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import firma.auth.TokenService;
import firma.exception.ConflictException;
import firma.role.Role;
import firma.role.RoleRepository;
import firma.user.dto.UserResponseDTO;
import firma.user.dto.UserSignUpDTO;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;
    
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "defaultRoleName", "user");
    }

    @Test
    @DisplayName("findAll should return empty list when no users")
    void findAllShouldReturnEmptyListWhenNoUsers() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponseDTO> users = userService.findAll();
        assertThat(users).isNotNull();
        assertThat(users).isEmpty();
    }

    @Test
    @DisplayName("findAll should map user fields correctly when user exists")
    void findAllShouldMapUserFieldsCorrectlyWhenUserExists() {
        User user = new User("username123", "password123");
        Role role = new Role("user");
        user.setRole(role);

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponseDTO> users = userService.findAll();
        assertThat(users.size()).isEqualTo(1);
        assertThat(users.get(0).username()).isEqualTo("username123");
    }

    @Test
    @DisplayName("create should return created user token when username is available")
    void createShouldReturnCreatedUserTokenWhenUsernameIsAvailable() {
        UserSignUpDTO data = new UserSignUpDTO("username123", "rawPassword");
        Role role = new Role("user");

        when(userRepository.existsByUsername(data.username())).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(roleRepository.findByName("user")).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tokenService.generateToken(any(User.class))).thenReturn("jwtToken");

        String token = userService.create(data);
        assertThat(token).isEqualTo("jwtToken");
        verify(passwordEncoder).encode("rawPassword");
    }

    @Test
    @DisplayName("create should return conflict exception when username is not available")
    void createShouldReturnConflictExceptionWhenUsernameIsNotAvailable() {
        UserSignUpDTO data = new UserSignUpDTO("username123", "password123");

        when(userRepository.existsByUsername(data.username())).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.create(data));

        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }
}
