package hung.learn.securityjwt.controllers;

import hung.learn.securityjwt.dtos.LoginRequestDTO;
import hung.learn.securityjwt.responses.MessageResponse;
import hung.learn.securityjwt.services.AuthenticationService;
import hung.learn.securityjwt.dtos.RegisterRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {
    @Autowired
    private AuthenticationService authService;

    @PostMapping("/auth/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDTO loginRequest) {
        try {
            String accessToken = authService.login(loginRequest);
            return ResponseEntity.ok(new MessageResponse(accessToken));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Invalid email or password"));
        }
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDTO registerRequest) {
        try {
            // Đăng ký người dùng
            authService.register(registerRequest);

            return ResponseEntity.ok(new MessageResponse("User registered successfully"));
        } catch (Exception e) {
            // Xử lý lỗi đăng ký
            return ResponseEntity.badRequest().body(new MessageResponse("Error during registration: " + e.getMessage()));
        }
    }
}
