package firma.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserSignUpDTO(
    @NotBlank(message = "Username must not be blank")
    @Size(min = 4, max = 16, message = "Username must be between 4 and 16 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username must only contain letters and numbers, without spaces")
    String username,

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    @Pattern(regexp = "^\\S+$", message = "Password must not contain spaces")
    String password
) {
    public UserSignUpDTO {
        username = username == null ? null : username.trim().toLowerCase();
    }
}
