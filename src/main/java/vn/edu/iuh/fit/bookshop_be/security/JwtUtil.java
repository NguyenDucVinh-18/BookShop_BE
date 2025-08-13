package vn.edu.iuh.fit.bookshop_be.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    private SecretKey secret;
    private  long accessTokenExp = 604800000 ; // 7 ngày access token
    private long refreshTokenExp = 604800000; // 7 ngày rf token ;
    @PostConstruct
    public void init() {
        if (SECRET_KEY == null || SECRET_KEY.isBlank()) {
            secret = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        } else {
            secret = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));
        }
    }
    // tạo Access Token
    public String generateAccessToken(String email , String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role) // Thêm role vào claims
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExp))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
    // tạo Refresh Token
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExp))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

    }

    // Trích xuất email từ token
    public String  extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secret) // sử dụng SecretKey
                .build()
//                .parseClaimsJwt(token)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    //Trích xuất danh sách role từ token
    public Set<String> extractRoles(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody()
                .get("roles", Set.class);
    }
    // kiểm tra token có hợp lệ không
    public boolean validateToken(String token , String email) {
        try{
            String tokenEmail = extractEmail(token);
//           return (tokenUserName.equals(userName) && isTokenExpired(token));
            return (tokenEmail.equals(email) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }
    // kiểm tra token hết hạn
    private boolean isTokenExpired(String token) {
        Date exp = Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return exp.before(new Date());
    }
    // trích xuất role từ token
    // Trích xuất role từ token
    public String extractRole(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("role", String.class);
        } catch (ExpiredJwtException e) {
            // Không ném ngoại lệ, trả về null để JwtRequestFilter xử lý
            return null;
        } catch (SignatureException e) {
            // Không ném ngoại lệ, trả về null để JwtRequestFilter xử lý
            return null;
        } catch (Exception e) {
            // Không ném ngoại lệ, trả về null để JwtRequestFilter xử lý
            return null;
        }
    }

}
