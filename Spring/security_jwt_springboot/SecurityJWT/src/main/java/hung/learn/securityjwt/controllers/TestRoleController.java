package hung.learn.securityjwt.controllers;

import hung.learn.securityjwt.services.JWTService;
import hung.learn.securityjwt.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TestRoleController {

    @Autowired
    private UserService userService;

    @Autowired
    private JWTService jwtService;


    @GetMapping("/public/admin")
    public ResponseEntity<String> adminEndpoint(@RequestHeader("Authorization") String token) {
        if (!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        List<String> roles = jwtService.getRolesFromToken(token);

        if (roles.contains("ADMIN")) {
            return ResponseEntity.ok("" + roles);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
    }

    @GetMapping("/public/user")
    public ResponseEntity<String> userEndpoint(@RequestHeader("Authorization") String token) {
        if (!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        List<String> roles = jwtService.getRolesFromToken(token);

        if (roles.contains("ADMIN")) {
            return ResponseEntity.ok("" + roles);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
    }

    @GetMapping("/private/admin")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String admin() {
        return "ADMIN đúng";
    }

    @GetMapping("/private/admin-read")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN_READ')")
    public ResponseEntity<String> adminReadEndpoint() {
        return ResponseEntity.ok("ADMIN_READ đúng");
    }

    @PostMapping("/private/admin-post")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN_POST')")
    public ResponseEntity<String> adminPostEndpoint() {
        return ResponseEntity.ok("ADMIN_POST đúng");
    }

    @PutMapping("/private/admin-put")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN_UPDATE')")
    public ResponseEntity<String> adminUpdateEndpoint() {
        return ResponseEntity.ok("ADMIN_UPDATE đúng");
    }

    @DeleteMapping("/private/admin-delete")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN_DELETE')")
    public ResponseEntity<String> adminDeleteEndpoint() {
        return ResponseEntity.ok("ADMIN_DELETE đúng");
    }

    @GetMapping("/private/user")
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    public String user() {
        return "USER đúng";
    }

}


/*
* 1. Claims trong token
* Trong JWT, bạn có thể thêm các claims để chứa thông tin về người dùng, bao gồm cả quyền (roles).
* Thông thường, các quyền được thêm vào dưới dạng một danh sách các chuỗi.
VD: "roles": ["ROLE_ADMIN", "ROLE_USER"],

 * 2. Kiểm tra quyền trong @PreAuthorize
* - Trong @PreAuthorize, bạn có thể sử dụng hasAnyAuthority hoặc hasAnyRole.
* Tuy nhiên, nếu bạn đang sử dụng JWT và claims là một danh sách,
* thì hasAnyRole không phải là lựa chọn thích hợp. Thay vào đó, bạn nên sử dụng hasAnyAuthority.
* - Đối với hasAnyAuthority, Spring Security sẽ kiểm tra xem người dùng có ít nhất một trong các quyền được liệt kê hay không.
* Nó không yêu cầu rằng tên quyền phải bắt đầu bằng "ROLE_" như hasAnyRole.

 * 3. Custom Claims và Phương thức UserDetails:
 * Nếu bạn muốn sử dụng các claims có tên khác, bạn có thể cấu hình JwtService hoặc một thành phần khác để đọc custom claims
 * và chuyển chúng thành đối tượng UserDetails mà Spring Security có thể hiểu được.
---> Tóm lại, bạn có thể sử dụng hasAnyAuthority và không cần phải bắt buộc quyền bắt đầu bằng "ROLE_".
* Đồng thời, bạn cũng có thể tự định nghĩa cách đọc và xử lý claims từ token trong dịch vụ JWT của mình.
* */