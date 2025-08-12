package vn.edu.iuh.fit.bookshop_be.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Lấy Header từ request
        final String authorizationHeader = request.getHeader("Authorization");
        String email = null;
        String jwt = null;
        String role = null;

        // Kiểm tra header có tồn tại và bắt đầu bằng "Bearer" không
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                email = jwtUtil.extractEmail(jwt);
                role = jwtUtil.extractRole(jwt);
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token hết hạn: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("JWT Token không hợp lệ: " + e.getMessage());
            }
        }

        // Nếu username và role được trích xuất thành công và chưa có authentication trong context
        if (email != null && role != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(jwt, email)) {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(email, null, Collections.singletonList(authority));
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }
        }

        // Tiếp tục chuỗi filter trong mọi trường hợp
        filterChain.doFilter(request, response);
    }
}
