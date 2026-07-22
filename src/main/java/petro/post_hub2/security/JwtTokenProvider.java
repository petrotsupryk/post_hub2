package petro.post_hub2.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import petro.post_hub2.model.constants.AuthenticationConstants;
import petro.post_hub2.model.entity.User;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtTokenProvider {
    private final SecretKey secretKey;
    private final long expirationTime;

    public JwtTokenProvider(@Value("${jwt.secret}") String jwtSecret,
                            @Value("${jwt.expiration}") long expirationTime) {
        this.expirationTime = expirationTime;
        this.secretKey = getSecretKey(jwtSecret);
    }

    public String generatedToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(AuthenticationConstants.USER_ID, user.getId());
        claims.put(AuthenticationConstants.USERNAME, user.getUsername());
        claims.put(AuthenticationConstants.EMAIL, user.getEmail());
        claims.put(AuthenticationConstants.USER_REGISTRATION_STATUS, user.getRegistrationStatus().name());
        claims.put(AuthenticationConstants.LAST_LOGIN, LocalDateTime.now().toString());
        List<String> roles = user.getRoles()
                .stream()
                .map(role -> role.getName())
                .toList();
        claims.put(AuthenticationConstants.ROLES, roles);
        return createToken(claims, user.getEmail());
    }

    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String refreshToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return createToken(claims, claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }

    public List<String> getRoles(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get(AuthenticationConstants.ROLES, List.class);
    }

    private SecretKey getSecretKey(String jwtSecret) {
        byte[] decodes64 = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(decodes64);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }
}
