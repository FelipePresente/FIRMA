package firma.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import firma.role.Role;
import firma.user.dto.UserResponseDTO;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("findAll should return empty list when no users")
    void findAllShouldReturnEmptyListWhenNoUsers() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponseDTO> users = userService.findAll();

        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    @DisplayName("findAll should map user fields correctly when user exists")
    void findAllShouldMapUserFieldsCorrectlyWhenUserExists() {
        User user = new User("username123", "password123");
        Role role = new Role("user");
        user.setRole(role);
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponseDTO> users = userService.findAll();

        assertEquals(1, users.size());
        assertEquals("username123", users.get(0).username());
    }
}
