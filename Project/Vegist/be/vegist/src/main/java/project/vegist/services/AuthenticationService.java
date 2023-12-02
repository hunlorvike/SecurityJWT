package project.vegist.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.vegist.dtos.LoginRequestDTO;
import project.vegist.dtos.RegisterRequestDTO;
import project.vegist.entities.User;
import project.vegist.exceptions.ResourceExistException;
import project.vegist.models.CustomUserDetail;

@Service
public class AuthenticationService {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public String login(LoginRequestDTO loginRequestDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // Directly use JwtService to generate the token
            return jwtService.generateToken((CustomUserDetail) userDetails);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password", e);
        }
    }

    public void register(RegisterRequestDTO registerRequestDTO) {
        User user = new User();
        user.setFullName(registerRequestDTO.getName());
        user.setEmail(registerRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));

        userService.saveUser(user);
    }

}
