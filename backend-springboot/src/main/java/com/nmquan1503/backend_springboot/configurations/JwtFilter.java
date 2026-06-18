package com.nmquan1503.backend_springboot.configurations;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nmquan1503.backend_springboot.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern; import java.util.stream.Collectors;
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtFilter extends OncePerRequestFilter {

    JwtUtil jwtUtil;

    private boolean isPublicEndpoint(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        if ("GET".equalsIgnoreCase(method)) {
            for (String endpoint : SecurityConfig.PUBLIC_GET_ENDPOINTS) {
                String regex = endpoint.replace("/**", "(/.*)?");
                if (Pattern.matches(regex, path)) return true;
            }
        }

        if ("POST".equalsIgnoreCase(method)) {
            for (String endpoint : SecurityConfig.PUBLIC_POST_ENDPOINTS) {
                String regex = endpoint.replace("/**", "(/.*)?");
                if (Pattern.matches(regex, path)) return true;
            }
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // ✅ 1. Skip public endpoint
        if (isPublicEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ 2. Lấy header
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        // ✅ 3. Verify token
        if (!jwtUtil.verifyToken(token)) {
            throw new BadCredentialsException("Invalid JWT token");
        }

        // ✅ 4. Lấy thông tin từ token
        JWTClaimsSet claims = jwtUtil.getClaimSetFromToken(token);

        Long userId;
        try {
            userId = Long.valueOf(claims.getSubject());
        } catch (NumberFormatException e) {
            throw new BadCredentialsException("Invalid user id in token");
        }

        // ⚠️ FIX QUAN TRỌNG NHẤT Ở ĐÂY
        String scope = claims.getClaim("scope").toString();

        List<SimpleGrantedAuthority> authorities =
                List.of(scope.split(" "))
                        .stream()
                        .map(role -> {
                            // đảm bảo luôn có ROLE_
                            if (!role.startsWith("ROLE_")) {
                                role = "ROLE_" + role;
                            }
                            return new SimpleGrantedAuthority(role);
                        })
                        .toList();

        // ✅ 5. Set vào SecurityContext
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // ✅ 6. Continue filter chain
        filterChain.doFilter(request, response);
    }
}