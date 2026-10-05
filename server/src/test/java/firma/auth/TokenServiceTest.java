package firma.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import firma.role.Role;
import firma.user.User;

public class TokenServiceTest {
    private TokenService tokenService;

    private static final String SECRET = "secret123";
    private static final String ISSUER = "FIRMA";
    private static final Long EXPIRATION_DAYS = 14L;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", SECRET);
        ReflectionTestUtils.setField(tokenService, "issuer", ISSUER);
        ReflectionTestUtils.setField(tokenService, "expirationDays", EXPIRATION_DAYS);
    }

    @Test
    @DisplayName("generateToken should return valid jwt token")
    void generateTokenShouldReturnValidJwtToken() {
        User user = new User("username123", "password123");
        Role role = new Role("user");
        user.setRole(role);
        user.setId(UUID.randomUUID());

        String token = tokenService.generateToken(user);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();

        DecodedJWT decoded = JWT.decode(token);
        assertThat(decoded.getSubject()).isEqualTo(user.getId().toString());
        assertThat(decoded.getIssuer()).isEqualTo(ISSUER);
        assertThat(decoded.getExpiresAt()).isAfter(Instant.now());

        Long daysUntilExpiration = ChronoUnit.DAYS.between(Instant.now(), decoded.getExpiresAtAsInstant());
        assertThat(daysUntilExpiration).isGreaterThanOrEqualTo(13L);
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

        assertThat(user.getId().toString()).isEqualTo(result);
    }

    @Test
    @DisplayName("validateToken should return null when token is expired")
    void validateTokenShouldReturnNullWhenTokenIsExpired() {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        String expiredToken = JWT.create()
                .withIssuer(ISSUER)
                .withSubject(UUID.randomUUID().toString())
                .withExpiresAt(Instant.now().minus(Duration.ofDays(1)))
                .sign(algorithm);

        String result = tokenService.validateToken(expiredToken);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("validateToken should return null when issuer is invalid")
    void validateTokenShouldReturnNullWhenIssuerIsInvalid() {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        String wrongIssuerToken = JWT.create()
                .withIssuer("wrongIssuer")
                .withSubject(UUID.randomUUID().toString())
                .withExpiresAt(Instant.now().plus(Duration.ofDays(1)))
                .sign(algorithm);

        String result = tokenService.validateToken(wrongIssuerToken);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("validateToken should return null when token is invalid or tampered")
    void validateTokenShouldReturnNullWhenTokenIsInvalidOrTampered() {
        Algorithm wrongAlgorithm = Algorithm.HMAC256("wrong-secret");
        String tamperedToken = JWT.create()
                .withIssuer(ISSUER)
                .withSubject(UUID.randomUUID().toString())
                .withExpiresAt(Instant.now().plus(Duration.ofDays(1)))
                .sign(wrongAlgorithm);

        String result = tokenService.validateToken(tamperedToken);
        assertThat(result).isNull();
    }
}
