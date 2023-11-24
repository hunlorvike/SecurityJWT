package hung.learn.securityjwt.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginRequestDTO {
    private String email;
    private String password;
}
