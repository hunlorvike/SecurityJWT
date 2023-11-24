package hung.learn.securityjwt.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RegisterRequestDTO {
    private String fullName;
    private String email;
    private String password;
}

