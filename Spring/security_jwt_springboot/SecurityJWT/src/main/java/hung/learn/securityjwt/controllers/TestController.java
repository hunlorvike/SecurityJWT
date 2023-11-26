package hung.learn.securityjwt.controllers;

import hung.learn.securityjwt.entities.Role;
import hung.learn.securityjwt.entities.User;
import hung.learn.securityjwt.models.CustomUserDetails;
import hung.learn.securityjwt.services.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.jsonwebtoken.Claims;

import java.util.Collections;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private JWTService jwtService;

    @GetMapping("/ok")
    public ResponseEntity<String> ok() {
        return ResponseEntity.ok("Test ok!");
    }

    @GetMapping("/error")
    public ResponseEntity<String> error() {
        throw new RuntimeException("Test error!");
    }

    @GetMapping("/token/gen")
    public ResponseEntity<String> genToken() {
        // Tạo một đối tượng User để tạo CustomUserDetails (thay thế bằng logic tạo đối tượng của bạn)
        User user = createTestUser();

        // Tạo một đối tượng CustomUserDetails từ User
        CustomUserDetails userDetails = new CustomUserDetails(user);

        // Gọi phương thức generateToken với CustomUserDetails
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(token);
    }

    private User createTestUser() {
        // Tạo một đối tượng User để sử dụng trong việc tạo CustomUserDetails
        User user = new User();
        user.setId(1L); // Điền các thông tin khác của user
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password"); // Bạn có thể muốn mã hóa mật khẩu trước khi lưu

        // Tạo một vai trò cho user (thực hiện logic tương ứng của bạn)
        Role role = new Role();
        role.setId(1L); // Điền các thông tin khác của vai trò
        role.setName("USER");

        // Thêm vai trò vào user
        user.setRoles(Collections.singletonList(role));

        return user;
    }

    @GetMapping("/token/claims")
    public ResponseEntity<Claims> claims(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(jwtService.getClaims(token));
    }

    @GetMapping("/get-username")
    public ResponseEntity<String> getUsernameFromToken(@RequestHeader("Authorization") String token) {
        String username = jwtService.getUsernameFromToken(token);
        return ResponseEntity.ok(username);
    }

    @GetMapping("/check-admin")
    public ResponseEntity<String> checkAdmin(@RequestHeader("Authorization") String token) {
        if (jwtService.isAdmin(token)) {
            return ResponseEntity.ok("User is an admin");
        } else {
            return ResponseEntity.ok("User is not an admin");
        }
    }

    @GetMapping("/get-user")
    public ResponseEntity<CustomUserDetails> getUserDetailsFromToken(@RequestHeader("Authorization") String token) {
        // Giải mã token để lấy thông tin user
        CustomUserDetails userDetails = jwtService.getUserDetailsFromToken(token);

        // Trả về thông tin user trong ResponseEntity
        return ResponseEntity.ok(userDetails);
    }

}
