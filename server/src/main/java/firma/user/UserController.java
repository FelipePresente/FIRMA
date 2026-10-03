package firma.user;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import firma.auth.CookieService;
import firma.user.dto.UserResponseDTO;
import firma.user.dto.UserSignUpDTO;

@RestController
@RequestMapping("users")
public class UserController {
    private final UserService userService;
    private final CookieService cookieService;

    public UserController(UserService userService, CookieService cookieService) {
        this.userService = userService;
        this.cookieService = cookieService;
    }

    @GetMapping
    // Requires admin role
    public List<UserResponseDTO> findAll() {
        return userService.findAll();
    }

    @PostMapping
    public ResponseEntity<Void> create(UserSignUpDTO data) {
        String token = userService.create(data);
        ResponseCookie jwtCookie = cookieService.generateCookie(token);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .build();
    }
}
