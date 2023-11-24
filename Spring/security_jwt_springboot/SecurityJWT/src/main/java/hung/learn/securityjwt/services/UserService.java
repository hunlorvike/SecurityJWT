package hung.learn.securityjwt.services;

import hung.learn.securityjwt.entities.Role;
import hung.learn.securityjwt.entities.User;
import hung.learn.securityjwt.exceptions.DuplicationException;
import hung.learn.securityjwt.exceptions.NotFoundException;
import hung.learn.securityjwt.models.CustomUserDetails;
import hung.learn.securityjwt.repositories.RoleRepository;
import hung.learn.securityjwt.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;


    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("User not found: " + id));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found: " + email));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User create(User user) {
        // Thiết lập role mặc định (USER) cho người dùng mới
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new NotFoundException("Role not found: USER"));

        user.setRoles(Collections.singletonList(userRole));

        checkEmailDuplication(user);
        return userRepository.save(user);
    }

    public User update(User user) {
        checkEmailDuplication(user);
        User p = findById(user.getId());
        p.setFullName(user.getFullName());
        p.setEmail(user.getEmail());
        p.setRoles(user.getRoles());
        return userRepository.save(p);
    }

    public void delete(Long id) {
        final User user = findById(id);
        userRepository.delete(user);
    }


    private void checkEmailDuplication(User user) {
        final String email = user.getEmail();
        if (email != null && email.length() > 0) {
            final Long id = user.getId();
            final User existingUser = userRepository.findByEmail(email).orElse(null);
            if (existingUser != null && Objects.equals(existingUser.getEmail(), email) && !Objects.equals(existingUser.getId(), id)) {
                throw new DuplicationException("Email duplication: " + email);
            }
        }
    }
}
