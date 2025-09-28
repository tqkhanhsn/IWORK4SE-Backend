package vn.iwork4se.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.iwork4se.controller.request.SignInRequest;
import vn.iwork4se.controller.response.TokenResponse;
import vn.iwork4se.service.AuthenticationService;
import vn.iwork4se.service.JwtService;
import vn.iwork4se.service.RefreshTokenService;

import java.io.IOException;


@RestController
@RequestMapping("/auth")
@Slf4j(topic = "AUTHENTICATION_CONTROLLER")
@Tag(name = "Authentication controller")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;


    @Operation(summary = "Access token", description = "API to get access token")
    @PostMapping("/login")
    public TokenResponse login(@RequestBody SignInRequest request) {
        log.info("Getting access token");
        return authenticationService.getAccessToken(request);
    }

    @Operation(summary = "Refresh token", description = "API to get refresh token")
    @PostMapping("/refresh-token")
    public TokenResponse getRefreshToken(@RequestBody String refreshToken) {
        log.info("Getting refresh token");
        return authenticationService.getRefreshToken(refreshToken);
    }


    @Operation(summary = "Logout", description = "API to logout user from current device")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader, HttpServletResponse response) throws IOException {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authenticationService.logout(authHeader);
                    SecurityContextHolder.clearContext();
                    log.info("User logged out successfully");
                    return ResponseEntity.ok("Logout successful");
            }


            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() &&
                    !authentication.getName().equals("anonymousUser")) {
                String username = authentication.getName();
                String platform = (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
                refreshTokenService.deleteRefreshToken(username, platform);
                SecurityContextHolder.clearContext();
                log.info("User {} logged out successfully", username);
                return ResponseEntity.ok("Logout successful");
            }

            return ResponseEntity.badRequest().body("No authenticated user found");
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Logout failed");
        }finally {
            response.sendRedirect("https://www.facebook.com/");
        }
    }

    @Operation(summary = "Logout from all devices", description = "API to logout user from all devices")
    @PostMapping("/logout-all")
    public ResponseEntity<String> logoutFromAllDevices(@RequestHeader("Authorization") String authHeader, HttpServletResponse response) throws IOException {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authenticationService.logoutFromAllDevices(authHeader);
                SecurityContextHolder.clearContext();
                log.info("User logged out from all devices successfully");
                return ResponseEntity.ok("Logout from all devices successful");
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                refreshTokenService.deleteAllRefreshTokensForUser(username);
                SecurityContextHolder.clearContext();
                log.info("User {} logged out from all devices successfully", username);
                return ResponseEntity.ok("Logout from all devices successful");
            }
            return ResponseEntity.badRequest().body("No authenticated user found");
        } catch (Exception e) {
            log.error("Error during logout from all devices: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Logout from all devices failed");
        }finally {
            response.sendRedirect("https://www.facebook.com/");
        }
    }

//    @Operation(summary = "Admin logout user", description = "API for admin to logout specific user")
//    @PostMapping("/admin/logout/{username}")
//    public ResponseEntity<String> adminLogoutUser(@PathVariable String username) {
//        try {
//            authenticationService.logoutFromAllDevices(username);
//            log.info("Admin logged out user: {}", username);
//            return ResponseEntity.ok("User " + username + " logged out successfully");
//        } catch (Exception e) {
//            log.error("Error during admin logout for user {}: {}", username, e.getMessage());
//            return ResponseEntity.internalServerError().body("Admin logout failed");
//        }
//    }
}
