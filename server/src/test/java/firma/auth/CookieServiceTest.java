package firma.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;
import org.springframework.test.util.ReflectionTestUtils;

public class CookieServiceTest {
    private CookieService cookieService;

    @BeforeEach
    void setUp() {
        cookieService = new CookieService();
        ReflectionTestUtils.setField(cookieService, "cookieName", "access_token");
        ReflectionTestUtils.setField(cookieService, "cookieSecure", false);
        ReflectionTestUtils.setField(cookieService, "cookieSameSite", "Strict");
    }

    @Test
    @DisplayName("generateCookie should return configurated cookie with token")
    void generateCookieShouldReturnConfiguredCookieWithToken() {
        String token = "jwt.valid.token";

        ResponseCookie cookie = cookieService.generateCookie(token);

        assertThat(cookie.getName()).isEqualTo("access_token");
        assertThat(cookie.getValue()).isEqualTo(token);
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.isSecure()).isFalse();
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.getMaxAge().getSeconds()).isEqualTo(60 * 60 * 24 * 14);
        assertThat(cookie.getSameSite()).isEqualTo("Strict");
    }

    @Test
    @DisplayName("deleteCookie should return cookie with max age zero and null value")
    void deleteCookieShouldReturnCookieWithMaxAgeZeroAndNullValue() {
        ResponseCookie cookie = cookieService.deleteCookie();

        assertThat(cookie.getName()).isEqualTo("access_token");
        assertThat(cookie.getValue()).isEmpty();
        assertThat(cookie.getMaxAge().getSeconds()).isZero();
        assertThat(cookie.isHttpOnly()).isTrue();
    }
}
