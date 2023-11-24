package hung.learn.securityjwt.services;

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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.util.Date;

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
        // Tạo chuỗi json web token từ email của user
        return Jwts.builder().setSubject(customUserDetails.getUsername()).setIssuedAt(now).setExpiration(expirationDate).signWith(getSecret(), SignatureAlgorithm.HS512).compact();
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
}
