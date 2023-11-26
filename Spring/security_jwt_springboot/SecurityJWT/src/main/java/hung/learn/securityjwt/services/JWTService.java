package hung.learn.securityjwt.services;

import hung.learn.securityjwt.entities.Role;
import hung.learn.securityjwt.entities.User;
import hung.learn.securityjwt.models.CustomUserDetails;
import hung.learn.securityjwt.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JWTService {
    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getSecret() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }


    public String generateToken(CustomUserDetails customUserDetails) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        // Lấy danh sách các role từ cơ sở dữ liệu thay vì từ UserDetails
        Collection<Role> rolesFromDatabase = customUserDetails.getUser().getRoles();
        List<String> roles = rolesFromDatabase.stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        // Tạo chuỗi json web token từ email và role của user
        return Jwts.builder()
                .setSubject(customUserDetails.getUsername())
                .claim("roles", roles)  // Thêm claim với key là "roles" và value là danh sách các role
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(getSecret(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String refreshExpiredToken(String expiredToken) {
        // Kiểm tra xem token đã hết hạn chưa
        if (!isTokenValid(expiredToken)) {
            // Giải mã token để lấy claims
            Claims claims = getClaims(expiredToken);

            // Lấy thông tin từ claims
            String username = claims.getSubject();

            // Lấy thông tin user từ repository
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

            // Tạo một đối tượng CustomUserDetails với thông tin từ user
            CustomUserDetails userDetails = new CustomUserDetails(user);

            // Tạo một token mới với thời gian sống mới
            return generateToken(userDetails);
        }

        // Nếu token chưa hết hạn, trả về token cũ
        return expiredToken;
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSecret()).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            // Token đã hết hạn
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecret())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // Handle token parsing exception
            throw new RuntimeException("Invalid token or expired token", e);
        }
    }

    public CustomUserDetails getUserDetailsFromToken(String token) {
        // Giải mã token để lấy claims
        Claims claims = Jwts.parserBuilder().setSigningKey(getSecret()).build().parseClaimsJws(token).getBody();

        // Lấy thông tin từ claims
        String username = claims.getSubject();

        // Lấy thông tin user từ repository
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Tạo một đối tượng CustomUserDetails với thông tin từ user
        return new CustomUserDetails(user);
    }

    public String getUsernameFromToken(String token) {
        // Giải mã token để lấy claims
        Claims claims = Jwts.parserBuilder().setSigningKey(getSecret()).build().parseClaimsJws(token).getBody();

        return claims.getSubject();
    }

    public List<String> getRolesFromToken(String token) {
        // Giải mã token để lấy claims
        Claims claims = Jwts.parserBuilder().setSigningKey(getSecret()).build().parseClaimsJws(token).getBody();

        // Lấy danh sách các role từ claims
        return (List<String>) claims.get("roles");
    }

    public boolean isAdmin(String token) {
        List<String> roles = getRolesFromToken(token);

        // Kiểm tra xem danh sách role có chứa "ADMIN" hay không
        return roles.contains("ADMIN");
    }

    public boolean isUser(String token) {
        List<String> roles = getRolesFromToken(token);

        // Kiểm tra xem danh sách role có chứa "USER" hay không
        return roles.contains("USER");
    }

    public boolean hasRole(String token, String roleName) {
        List<String> roles = getRolesFromToken(token);

        // Kiểm tra xem danh sách role có chứa roleName hay không
        return roles.contains(roleName);
    }


}
