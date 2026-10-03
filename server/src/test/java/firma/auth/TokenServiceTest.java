package firma.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import firma.role.Role;
import firma.user.User;

public class TokenServiceTest {
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "secret123");
        ReflectionTestUtils.setField(tokenService, "issuer", "FIRMA");
        ReflectionTestUtils.setField(tokenService, "expirationDays", 14L);
    }

    @Test
    @DisplayName("generateToken should return valid jwt token")
    void generateTokenShouldReturnValidJwtToken() {
        User user = new User("username123", "password123");
        Role role = new Role("user");
        user.setRole(role);
        user.setId(UUID.randomUUID());

        String token = tokenService.generateToken(user);

        assertNotNull(token);
        assertThat(token).isNotEmpty();
    }

    @Test
    @DisplayName("validateToken should return subject when token is valid")
    void validateTokenShouldReturnSubjectWhenTokenIsValid() {
        User user = new User("username123", "password123");
        Role role = new Role("user");
        user.setRole(role);
        user.setId(UUID.randomUUID());

        String token = tokenService.generateToken(user);

        String result = tokenService.validateToken(token);

        assertEquals(result, user.getId().toString());
    }

    @Test
    void validateTokenShouldReturnNullWhenTokenIsInvalidOrTampered() {
        String invalidToken = "invalid.token";

        String result = tokenService.validateToken(invalidToken);

        assertNull(result);
    }
}
