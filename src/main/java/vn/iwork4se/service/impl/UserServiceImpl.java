package vn.iwork4se.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iwork4se.common.UserStatus;
import vn.iwork4se.common.UserType;
import vn.iwork4se.controller.request.ChangePasswordRequest;
import vn.iwork4se.controller.request.SignInRequest;
import vn.iwork4se.controller.request.UserCreationRequest;
import vn.iwork4se.controller.response.EmailVerificationResponse;
import vn.iwork4se.controller.response.TokenResponse;
import vn.iwork4se.controller.response.UserCreationResponse;
import vn.iwork4se.exception.BadRequestException;
import vn.iwork4se.exception.ResourceNotFoundException;
import vn.iwork4se.model.Applicant;
import vn.iwork4se.model.Employer;
import vn.iwork4se.model.Role;
import vn.iwork4se.model.User;
import vn.iwork4se.repository.RoleRepository;
import vn.iwork4se.repository.UserRepository;
import vn.iwork4se.service.AuthenticationService;
import vn.iwork4se.service.EmailService;
import vn.iwork4se.service.UserService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static vn.iwork4se.common.UserType.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final AuthenticationService authenticationService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserCreationResponse createUser(UserCreationRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByUserName(req.getUserName())) {
            throw new RuntimeException("Username already exists");
        }

        User user = null;
        if(req.getUserType().equals(APPLICANT)){
            user = new Applicant();
            user.setId("APP-"+ UUID.randomUUID().toString());
            user.setUserType(APPLICANT);
            Role applicantRole = roleRepository.findById(2L)
                    .orElseThrow(() -> new RuntimeException("Role Applicant not found"));
            user.setRole(applicantRole);
        }else if(req.getUserType().equals(EMPLOYER)){
            user = new Employer();
            user.setId("EMP-"+ UUID.randomUUID().toString());
            user.setUserType(EMPLOYER);
            Role employerRole = roleRepository.findById(3L)
                    .orElseThrow(() -> new RuntimeException("Role Employer not found"));
            user.setRole(employerRole);
        }else {
            user = new User();
            user.setId("AD-"+ UUID.randomUUID().toString());
            user.setUserType(ADMIN);
            Role adminRole = roleRepository.findById(1L)
                    .orElseThrow(() -> new RuntimeException("Role Employer not found"));
            user.setRole(adminRole);

        }

        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setEmail(req.getEmail());
        user.setUserName(req.getUserName());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setUserStatus(UserStatus.INACTIVE);


        User savedUser = userRepository.save(user);

        SignInRequest request = SignInRequest.builder()
                .username(req.getUserName())
                .password(req.getPassword())
                .platform(req.getPlatform())
                .deviceToken(req.getDeviceToken())
                .versionApp(req.getVersionApp())
                .build();

        TokenResponse tokenResponse = authenticationService.getAccessToken(request);
        try {
            emailService.emailVerification(tokenResponse.getAccessToken(),req.getEmail(),req.getFirstName()+" "+ req.getLastName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return UserCreationResponse.builder()
                .id(savedUser.getId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .userName(savedUser.getUsername())
                .userType(req.getUserType())
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .build();
    }

    @Override
    public void changePasswordEmployer(ChangePasswordRequest req) {
        log.info("Changing password for user: {}", req);

        User user = getUser(req.getId());

        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }

        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            throw new BadRequestException("Password and confirm password do not match");
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);

        log.info("Changed password for user: {}", user);
    }

    public boolean verifySecretCode(String secretCode,String email) {
        String redisKey = "email_verification:" + email;
        String storedSecretCode = (String) redisTemplate.opsForValue().get(redisKey);
        if (storedSecretCode == null) {
            return false;
        }
        return storedSecretCode.equals(secretCode);
    }
    private User getUser(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: "));
    }
}
