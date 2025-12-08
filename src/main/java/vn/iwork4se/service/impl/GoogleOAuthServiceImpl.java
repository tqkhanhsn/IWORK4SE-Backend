package vn.iwork4se.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iwork4se.common.UserStatus;
import vn.iwork4se.common.UserType;
import vn.iwork4se.controller.response.TokenResponse;
import vn.iwork4se.exception.InvalidDataException;
import vn.iwork4se.model.Applicant;
import vn.iwork4se.model.Role;
import vn.iwork4se.model.User;
import vn.iwork4se.repository.RoleRepository;
import vn.iwork4se.repository.UserRepository;
import vn.iwork4se.service.GoogleOAuthService;
import vn.iwork4se.service.JwtService;
import vn.iwork4se.service.RefreshTokenService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "GOOGLE_OAUTH_SERVICE")
public class GoogleOAuthServiceImpl implements GoogleOAuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Value("${google.oauth.client-id}")
    private String googleClientId;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenResponse loginWithGoogle(String idToken, String platform, String deviceToken, String versionApp) {
        log.info("Processing Google login with token");

        try {
            GoogleIdToken googleToken = verifyGoogleToken(idToken);
            if (googleToken == null) {
                log.error("Invalid Google token");
                throw new InvalidDataException("Token Google không hợp lệ");
            }

            GoogleIdToken.Payload payload = googleToken.getPayload();
            String email = payload.getEmail();
            String givenName = (String) payload.get("given_name");
            String familyName = (String) payload.get("family_name");

            log.info("Google token verified for email: {}", email);
            User user = userRepository.findByEmail(email);
            if (user == null) {
                log.info("User not found with email: {}. Creating new user", email);
                user = createNewGoogleUser(email, givenName, familyName);
            } else {
                if (user.getUserStatus() == UserStatus.BANNED) {
                    log.warn("User {} attempted to login but is BANNED", email);
                    throw new InvalidDataException("Tài khoản của bạn đã bị khóa tạm thời");
                }
                if (user.getUserStatus() == UserStatus.DELETED) {
                    log.warn("User {} attempted to login but is DELETED", email);
                    throw new InvalidDataException("Tài khoản của bạn đã bị khóa vĩnh viễn");
                }
                log.info("User found with email: {}. Existing user login", email);
            }

            log.info("Generating JWT token for Google user: {}", email);
            List<String> authorities = new ArrayList<>();
            user.getAuthorities().forEach(authority -> authorities.add(authority.getAuthority()));

            String accessToken = jwtService.generateAccessToken(user.getUsername(), platform, authorities);
            String refreshToken = jwtService.generateRefreshToken(user.getUsername(), platform, authorities);

            try {
                refreshTokenService.storeRefreshToken(user.getUsername(), platform, refreshToken);
                log.info("Refresh token stored in Redis for Google user: {}", user.getUsername());
            } catch (Exception e) {
                log.error("Failed to store refresh token in Redis for user: {}, error: {}", user.getUsername(), e.getMessage());
            }

            return TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .role(user.getRole().getName())
                    .userId(user.getId())
                    .fullName(user.getLastName() + " " + user.getFirstName())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .build();

        } catch (Exception e) {
            log.error("Error during Google login: {}", e.getMessage());
            throw new InvalidDataException("Lỗi khi đăng nhập với Google: " + e.getMessage());
        }
    }


    private GoogleIdToken verifyGoogleToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new JacksonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                log.info("Google token verified successfully");
                return idToken;
            } else {
                log.error("Invalid ID token.");
                return null;
            }
        } catch (Exception e) {
            log.error("Error verifying Google token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Create new user from Google login (auto-register)
     */
    private User createNewGoogleUser(String email, String firstName, String lastName) {
        String baseUsername = email.split("@")[0];
        String username = baseUsername;
        int counter = 1;

        while (userRepository.existsByUserName(username)) {
            username = baseUsername + counter;
            counter++;
        }

        Applicant applicant = new Applicant();
        applicant.setId("APP-" + UUID.randomUUID().toString());
        applicant.setUserType(UserType.APPLICANT);

        Role applicantRole = roleRepository.findById(2L)
                .orElseThrow(() -> new RuntimeException("Role Applicant not found"));
        applicant.setRole(applicantRole);

        applicant.setFirstName(firstName != null ? firstName : "");
        applicant.setLastName(lastName != null ? lastName : "");
        applicant.setEmail(email);
        applicant.setUserName(username);
        String randomPassword = UUID.randomUUID().toString();
        applicant.setPassword(passwordEncoder.encode(randomPassword));
        applicant.setUserStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(applicant);
        log.info("Created new Google user with email: {} and username: {}", email, username);

        return savedUser;
    }
}
