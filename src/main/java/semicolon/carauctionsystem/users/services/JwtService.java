package semicolon.carauctionsystem.users.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import semicolon.carauctionsystem.users.security.UserPrincipal;

import java.util.Date;

@Service
public class JwtService {

    private final String SECRET_KEY;

    public JwtService ( @Value("${JWT_SECRET_KEY}")  String secretKey) {
        this.SECRET_KEY = secretKey;
    }

    public String generateToken(UserPrincipal userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("name", userDetails.getName())
                .claim("roles",userDetails.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY).build()
                .parseClaimsJws(token).getBody()
                .getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String userName = extractUsername(token);
        return userName.equals(userDetails.getUsername())&& !isTokenExpired(token);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY).build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Date extractExpiration(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY).build()
                .parseClaimsJws(token).getBody().getExpiration();
    }

    private boolean isTokenExpired(String token) {
        Date tokensExpiryDate =  extractExpiration(token);
        return tokensExpiryDate.before(new Date());
    }
}
