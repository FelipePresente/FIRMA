package firma.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service 
public class CookieService {

    @Value("${api.security.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${api.security.cookie.samesite:Strict}")
    private String cookieSameSite;

    @Value("${api.security.auth.cookie.name:access_token}")
    private String cookieName;

    public ResponseCookie generateCookie(String token) {
        return ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(60 * 60 * 24 * 14)
                .sameSite(cookieSameSite)
                .build();
    }

    public ResponseCookie deleteCookie() {
        return ResponseCookie.from(cookieName, null)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite(cookieSameSite)
                .build();
    }
}
