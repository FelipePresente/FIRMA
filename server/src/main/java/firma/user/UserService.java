package firma.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import firma.role.RoleRepository;
import firma.user.dto.UserResponseDTO;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Value("${app.roles.default}")
    private String defaultRoleName;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<UserResponseDTO> findAll() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(UserResponseDTO::fromEntity)
                .toList();
    }
}
