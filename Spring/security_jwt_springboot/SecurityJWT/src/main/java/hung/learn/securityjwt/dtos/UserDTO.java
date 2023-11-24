package hung.learn.securityjwt.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDTO {
    private Long id;
    private String fullName;
    private String email;
    private String password;
    private String role;
}

