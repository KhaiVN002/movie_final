package com.nmquan1503.backend_springboot.services.authentication;

import com.nmquan1503.backend_springboot.dtos.requests.authentication.AuthenticationRequest;
import com.nmquan1503.backend_springboot.dtos.requests.authentication.IntrospectRequest;
import com.nmquan1503.backend_springboot.dtos.responses.authentication.AuthenticationResponse;
import com.nmquan1503.backend_springboot.dtos.responses.authentication.IntrospectResponse;
import com.nmquan1503.backend_springboot.entities.authentication.RefreshToken;
import com.nmquan1503.backend_springboot.entities.user.Role;
import com.nmquan1503.backend_springboot.entities.user.User;
import com.nmquan1503.backend_springboot.exceptions.GeneralException;
import com.nmquan1503.backend_springboot.exceptions.ResponseCode;
import com.nmquan1503.backend_springboot.services.user.RoleService;
import com.nmquan1503.backend_springboot.services.user.UserService;
import com.nmquan1503.backend_springboot.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    UserService userService;
    PasswordEncoder passwordEncoder;
    JwtUtil jwtUtil;
    RefreshTokenService refreshTokenService;
    RoleService roleService;

    @NonFinal
    @Value("${jwt.refreshToken.expirationTime}")
    int refreshTokenExpirationTime;

    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        User user = userService.fetchUserByPhoneOrEmail(request.getIdentifier());
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new GeneralException(ResponseCode.UNAUTHENTICATED);
        }
        List<Role> roles = roleService.fetchRolesOfUserId(user.getId());
        String accessToken = jwtUtil.generateAccessToken(user, roles);
        String deviceInfo = httpServletRequest.getHeader("User-Agent");
        RefreshToken refreshToken = jwtUtil.generateRefreshToken(user, deviceInfo);
        refreshTokenService.save(refreshToken);
        httpServletResponse.addCookie(createRefreshTokenCookie(httpServletRequest, refreshToken.getId(), refreshTokenExpirationTime));
        return AuthenticationResponse.builder()
                .authenticated(true)
                .accessToken(accessToken)
                .build();
    }

    public IntrospectResponse introspect(IntrospectRequest request) {
        String accessToken = request.getAccessToken();
        return IntrospectResponse.builder()
                .valid(jwtUtil.verifyToken(accessToken))
                .build();
    }

    public AuthenticationResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String refreshTokenId = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshTokenId = cookie.getValue();
                    break;
                }
            }
        }
        if (refreshTokenId == null) {
            throw new GeneralException(ResponseCode.UNAUTHORIZED);
        }
        RefreshToken refreshToken = refreshTokenService.fetchRefreshTokenById(refreshTokenId);
        String deviceInfo = request.getHeader("User-Agent");
        deviceInfo = deviceInfo != null ? deviceInfo : "Unknown Device";
        if (refreshToken.getExpirationTime().isBefore(LocalDateTime.now())
                || !refreshToken.getDeviceInfo().equals(deviceInfo)) {
            throw new GeneralException(ResponseCode.UNAUTHORIZED);
        }
        refreshTokenService.updateExpirationTime(refreshToken);
        response.addCookie(createRefreshTokenCookie(request, refreshToken.getId(), refreshTokenExpirationTime));
        User user = refreshToken.getUser();
        List<Role> roles = roleService.fetchRolesOfUserId(user.getId());
        return AuthenticationResponse.builder()
                .authenticated(true)
                .accessToken(jwtUtil.generateAccessToken(user, roles))
                .build();
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String refreshTokenId = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshTokenId = cookie.getValue();
                    break;
                }
            }
        }
        refreshTokenService.deleteById(refreshTokenId);
        Cookie cookie = createRefreshTokenCookie(request, null, 0);
        response.addCookie(cookie);
    }

    private Cookie createRefreshTokenCookie(HttpServletRequest request, String value, int maxAge) {
        Cookie cookie = new Cookie("refreshToken", value);
        cookie.setHttpOnly(true);
        cookie.setSecure(isHttpsRequest(request));
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "Lax");
        return cookie;
    }

    private boolean isHttpsRequest(HttpServletRequest request) {
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        return request.isSecure() || "https".equalsIgnoreCase(forwardedProto);
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal()==null || !(authentication.getPrincipal() instanceof Number)) {
            throw new GeneralException(ResponseCode.UNAUTHORIZED);
        }
        return (Long) authentication.getPrincipal();
    }

}
