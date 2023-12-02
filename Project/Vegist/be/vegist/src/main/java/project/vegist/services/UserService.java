package project.vegist.services;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.vegist.entities.Role;
import project.vegist.entities.User;
import project.vegist.entities.UserRole;
import project.vegist.repositories.RoleRepository;
import project.vegist.repositories.UserRepository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public boolean userExistsByEmail(@NotEmpty String email) {
        return userRepository.existsByEmail(email);
    }

    public void saveUser(User user) {
        // Check if userRoles is null and initialize it as an empty list
        if (user.getUserRoles() == null) {
            user.setUserRoles(new ArrayList<>());
        }

        Optional<Role> userRole = roleRepository.findByRoleName("USER");

        // Create a new userRole only if it doesn't exist
        if (userRole.isEmpty()) {
            Role newUserRole = new Role();
            newUserRole.setRoleName("USER");
            roleRepository.save(newUserRole);
            userRole = Optional.of(newUserRole);
        }

        // Check if the "USER" role is already present
        boolean hasUserRole = user.getUserRoles().stream()
                .anyMatch(userRoleEntity -> userRoleEntity.getRole().getRoleName().equals("USER"));

        // Add the "USER" role if not already present
        if (!hasUserRole) {
            UserRole newUserRole = new UserRole();
            newUserRole.setUser(user);
            newUserRole.setRole(userRole.get());
            user.getUserRoles().add(newUserRole);
        }

        // Lưu User vào cơ sở dữ liệu
        userRepository.save(user);
    }

}
