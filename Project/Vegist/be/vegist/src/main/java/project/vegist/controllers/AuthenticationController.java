package project.vegist.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.vegist.dtos.LoginRequestDTO;
import project.vegist.dtos.RegisterRequestDTO;
import project.vegist.exceptions.ResourceExistException;
import project.vegist.responses.BaseResponse;
import project.vegist.responses.ErrorResponse;
import project.vegist.responses.SuccessResponse;
import project.vegist.services.AuthenticationService;
import project.vegist.services.UserService;

@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationService authService;

    @Autowired
    private UserService userService;


    @PostMapping("/auth/login")
    public ResponseEntity<BaseResponse<String>> authenticateUser(@RequestBody LoginRequestDTO loginRequest) {
        try {
            String accessToken = authService.login(loginRequest);

            // Return success response with the access token
            return ResponseEntity.ok(new SuccessResponse<>(accessToken));
        } catch (AuthenticationException e) {
            // Return unauthorized response
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse<>("Authentication failed"));
        }
    }

    @PostMapping("/auth/register")
    public ResponseEntity<BaseResponse<String>> registerUser(@RequestBody RegisterRequestDTO registerRequest) {
        try {
            // Check if the user already exists
            if (userService.userExistsByEmail(registerRequest.getEmail())) {
                throw new ResourceExistException(registerRequest.getEmail(), HttpStatus.CONFLICT);
            }

            // Register the user
            authService.register(registerRequest);

            // Return success response
            return ResponseEntity.ok(new SuccessResponse<>("User registered successfully"));
        } catch (ResourceExistException e) {
            // Handle the case where the email already exists
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse<>("Email already exists: " + e.getMessage()));
        } catch (Exception e) {
            // Handle other exceptions and return ErrorResponse
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse<>("Error during user registration: " + e.getMessage()));
        }
    }


}
