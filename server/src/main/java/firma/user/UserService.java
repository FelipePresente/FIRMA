package firma.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import firma.auth.TokenService;
import firma.exception.ConflictException;
import firma.exception.ResourceNotFoundException;
import firma.role.Role;
import firma.role.RoleRepository;
import firma.user.dto.UserResponseDTO;
import firma.user.dto.UserSignUpDTO;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.roles.default}")
    private String defaultRoleName;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, TokenService tokenService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public List<UserResponseDTO> findAll() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(UserResponseDTO::fromEntity)
                .toList();
    }

    public String create(UserSignUpDTO data) {
        if (userRepository.existsByUsername(data.username())) {
            throw new ConflictException("Username already exists");
        }

        String encryptedPassword = passwordEncoder.encode(data.password());

        User user = new User(data.username(), encryptedPassword);

        Role role = roleRepository.findByName(defaultRoleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role 'user' not found"));

        user.setRole(role);

        User createdUser = userRepository.save(user);
        return tokenService.generateToken(createdUser);
    }
}
