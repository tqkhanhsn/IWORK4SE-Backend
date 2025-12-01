package vn.iwork4se.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.iwork4se.common.TokenType;
import vn.iwork4se.controller.request.SignInRequest;
import vn.iwork4se.controller.response.TokenResponse;
import vn.iwork4se.exception.ForBiddenException;
import vn.iwork4se.exception.InvalidDataException;
import vn.iwork4se.model.User;
import vn.iwork4se.repository.UserRepository;
import vn.iwork4se.service.AuthenticationService;
import vn.iwork4se.service.JwtService;
import vn.iwork4se.service.RefreshTokenService;

import java.util.ArrayList;
import java.util.List;

import static vn.iwork4se.common.TokenType.REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION_SERVICE")
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public TokenResponse getAccessToken(SignInRequest request) {
        log.info("Get access token");

        List<String> authorities = new ArrayList<>();
        try {

            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            log.info("isAuthenticated = {}", authenticate.isAuthenticated());
            log.info("Authorities: {}", authenticate.getAuthorities().toString());
            authorities.add(authenticate.getAuthorities().toString());
            ((UsernamePasswordAuthenticationToken) authenticate).setDetails(request.getPlatform());

            SecurityContextHolder.getContext().setAuthentication(authenticate);
        } catch (BadCredentialsException | DisabledException e) {
            log.error("errorMessage: {}", e.getMessage());
            throw new AccessDeniedException(e.getMessage());
        }
        User user = userRepository.findByUserName(request.getUsername());
        
        // Kiểm tra trạng thái user trước khi cho phép đăng nhập
        if (user.getUserStatus() == vn.iwork4se.common.UserStatus.BANNED) {
            log.warn("User {} attempted to login but is BANNED. Unban date: {}", 
                    request.getUsername(), user.getUnbannedDate());
            throw new AccessDeniedException("Tài khoản của bạn đã bị khóa tạm thời trong 7 ngày. Ngày hết hạn: " + 
                    (user.getUnbannedDate() != null ? user.getUnbannedDate().toString() : "N/A"));
        }
        
        if (user.getUserStatus() == vn.iwork4se.common.UserStatus.DELETED) {
            log.warn("User {} attempted to login but is DELETED", request.getUsername());
            throw new AccessDeniedException("Tài khoản của bạn đã bị khóa vĩnh viễn");
        }
        
        String accessToken = jwtService.generateAccessToken(request.getUsername(),request.getPlatform(), authorities);
        String refreshToken = jwtService.generateRefreshToken(request.getUsername(),request.getPlatform(), authorities);
        try {
            refreshTokenService.storeRefreshToken(request.getUsername(),request.getPlatform(), refreshToken);
            log.info("Refresh token stored in Redis for user: {}", request.getUsername());
        } catch (Exception e) {
            log.error("Failed to store refresh token in Redis for user: {}, error: {}", request.getUsername(), e.getMessage());
        }

        return TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).role(user.getRole().getName()).userId(user.getId()).fullName(user.getLastName()+" "+user.getFirstName()).email(user.getEmail()).phone(user.getPhone()).build();
    }


    @Override
    public TokenResponse getRefreshToken(String refreshToken) {
        log.info("Get refresh token");

        if (!StringUtils.hasLength(refreshToken)) {
            throw new InvalidDataException("Token must be not blank");
        }

        try {
            String userName = jwtService.extractUsername(refreshToken, REFRESH_TOKEN);
            String platform = jwtService.extractPlatform(refreshToken, REFRESH_TOKEN);
            if (!refreshTokenService.validateRefreshToken(userName,platform, refreshToken)) {
                log.error("Invalid refresh token for user: {}", userName);
                throw new ForBiddenException("Invalid refresh token");
            }
            User user = userRepository.findByUserName(userName);
            if (user == null) {
                log.error("User not found: {}", userName);
                throw new ForBiddenException("User not found");
            }

            List<String> authorities = new ArrayList<>();
            user.getAuthorities().forEach(authority -> authorities.add(authority.getAuthority()));

            String accessToken = jwtService.generateAccessToken(user.getUsername(),platform, authorities);

            String newRefreshToken = jwtService.generateRefreshToken(user.getUsername(),platform, authorities);
            refreshTokenService.storeRefreshToken(userName,platform, newRefreshToken);
            log.info("New refresh token generated and stored for user: {}", userName);

            return TokenResponse.builder().accessToken(accessToken).refreshToken(newRefreshToken).build();
        } catch (Exception e) {
            log.error("Access denied! errorMessage: {}", e.getMessage());
            throw new ForBiddenException(e.getMessage());
        }
    }

    public void logout(String authHeader) {
        String username = "";
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7).trim();
                username = jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);
                String platform = jwtService.extractPlatform(token, TokenType.ACCESS_TOKEN);
                refreshTokenService.deleteRefreshToken(username, platform);
                log.info("User logged out successfully: {}", username);
            }
        } catch (Exception e) {
            log.error("Error during logout for user: {}, error: {}", username, e.getMessage());
        }
    }

    public void logoutFromAllDevices(String authHeader) {
        String username = "";
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7).trim();
                username = jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);
                refreshTokenService.deleteAllRefreshTokensForUser(username);
                log.info("User logged out from all devices: {}", username);
            }
        } catch (Exception e) {
            log.error("Error during logout from all devices for user: {}, error: {}", username, e.getMessage());
        }
    }
}
