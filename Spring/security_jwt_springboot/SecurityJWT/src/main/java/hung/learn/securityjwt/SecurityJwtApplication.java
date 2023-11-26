package hung.learn.securityjwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class SecurityJwtApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityJwtApplication.class, args);
    }

}

/*
 * Spring Security sử dụng GrantedAuthority để biểu diễn quyền (role).
 * Khi bạn tạo một Authentication object (ví dụ: UsernamePasswordAuthenticationToken),
 * bạn cung cấp một danh sách các GrantedAuthority mà người dùng có.

 * Khi sử dụng hasAnyRole hoặc hasAnyAuthority trong @PreAuthorize,
 * Spring Security sẽ tự động so sánh danh sách các quyền (role) mà người dùng có (được lưu trữ trong GrantedAuthority)
 * với danh sách các quyền bạn cung cấp trong hasAnyRole hoặc hasAnyAuthority.
 * */