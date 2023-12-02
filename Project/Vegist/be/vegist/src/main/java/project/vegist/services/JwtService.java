package project.vegist.services;

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
import project.vegist.entities.User;
import project.vegist.models.CustomUserDetail;
import project.vegist.repositories.UserRepository;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class JwtService {
    @Autowired
    private UserRepository userRepository;
    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getSecret() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(CustomUserDetail customUserDetail) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        Collection<String> roles = customUserDetail.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toCollection(HashSet::new));

        String token = Jwts.builder()
                .setSubject(String.valueOf(customUserDetail.getUser().getId()))
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(getSecret(), SignatureAlgorithm.HS512)
                .compact();
        return "Bearer " + token;
    }

    public String refreshExpiredToken(String expiredToken) {
        // Kiểm tra xem token đã hết hạn chưa
        if (!isTokenValid(expiredToken)) {
            // Giải mã token để lấy claims
            Claims claims = getClaims(expiredToken);

            // Lấy thông tin từ claims
            String userId = claims.getSubject();

            // Lấy thông tin user từ repository
            User user = userRepository.findById(Long.valueOf(userId))
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));

            // Tạo một đối tượng CustomUserDetails với thông tin từ user
            CustomUserDetail userDetails = new CustomUserDetail(user);

            // Tạo một token mới với thời gian sống mới
            return generateToken(userDetails);
        }

        // Nếu token chưa hết hạn, trả về token cũ
        return expiredToken;
    }

    public boolean isTokenValid(String token) {
        try {
            // Remove the "Bearer " prefix
            String cleanedToken = removeBearerPrefix(token);

            // Parse the token and check for validity
            Jwts.parserBuilder().setSigningKey(getSecret()).build().parseClaimsJws(cleanedToken);

            return true;
        } catch (ExpiredJwtException ex) {
            // Token has expired
            return false;
        } catch (Exception e) {
            // Token is invalid for some other reason
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("JWT token is null or empty");
        }

        // Giải mã token để lấy claims
        Claims claims = Jwts.parserBuilder().setSigningKey(getSecret()).build().parseClaimsJws(removeBearerPrefix(token)).getBody();

        // Get the subject (user ID) from claims
        String userIdStr = claims.getSubject();

        if (userIdStr == null) {
            throw new IllegalArgumentException("User ID not found in the token");
        }

        return Long.parseLong(userIdStr);
    }

    public Claims getClaims(String token) {
        try {
            // Remove the "Bearer " prefix if present
            String tokenWithoutPrefix = removeBearerPrefix(token);

            // Parse the claims from the token
            return Jwts.parserBuilder()
                    .setSigningKey(getSecret())
                    .build()
                    .parseClaimsJws(tokenWithoutPrefix)
                    .getBody();
        } catch (Exception e) {
            // Handle token parsing exception
            throw new RuntimeException("Invalid token or expired token", e);
        }
    }


    private String removeBearerPrefix(String token) {
        // Remove the "Bearer " prefix
        return token.replace("Bearer ", "");
    }

}
