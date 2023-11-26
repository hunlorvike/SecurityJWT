package hung.learn.securityjwt.services;

import hung.learn.securityjwt.dtos.RegisterRequestDTO;
import hung.learn.securityjwt.entities.User;
import hung.learn.securityjwt.models.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import hung.learn.securityjwt.dtos.LoginRequestDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;

@Service
public class AuthenticationService {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;


    public String login(LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Directly use JwtService to generate the token
            return jwtService.generateToken((CustomUserDetails) userDetails);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password", e);
        }
    }

    public void register(RegisterRequestDTO registerRequest) {
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        userService.create(user);
    }

}
