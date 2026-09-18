package firma.user.dto;

import java.util.UUID;

import firma.user.User;

public record UserResponseDTO(
    UUID id,
    String username,
    String roleName
) {
    public static UserResponseDTO fromEntity(User user) {
        return new UserResponseDTO(
            user.getId(),
            user.getUsername(),
            user.getRole().getName()
        );
    }
}
