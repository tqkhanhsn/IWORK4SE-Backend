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
import vn.iwork4se.controller.request.ApplicantCreationRequest;
import vn.iwork4se.controller.request.ChangePasswordRequest;
import vn.iwork4se.controller.request.EmployerCreationRequest;
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
import vn.iwork4se.service.NotificationService;
import vn.iwork4se.service.UserService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
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
    private final NotificationService notificationService;

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
    @Transactional(rollbackFor = Exception.class)
    public UserCreationResponse createApplicant(ApplicantCreationRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByUserName(req.getUserName())) {
            throw new RuntimeException("Username already exists");
        }

        Applicant applicant = new Applicant();
        applicant.setId("APP-" + UUID.randomUUID().toString());
        applicant.setUserType(APPLICANT);

        Role applicantRole = roleRepository.findById(2L)
                .orElseThrow(() -> new RuntimeException("Role Applicant not found"));
        applicant.setRole(applicantRole);

        applicant.setFirstName(req.getFirstName());
        applicant.setLastName(req.getLastName());
        applicant.setEmail(req.getEmail());
        applicant.setUserName(req.getUserName());
        applicant.setPassword(passwordEncoder.encode(req.getPassword()));
        applicant.setUserStatus(UserStatus.INACTIVE);

        Applicant savedApplicant = userRepository.save(applicant);

        SignInRequest signInRequest = SignInRequest.builder()
                .username(req.getUserName())
                .password(req.getPassword())
                .platform(req.getPlatform())
                .deviceToken(req.getDeviceToken())
                .versionApp(req.getVersionApp())
                .build();

        TokenResponse tokenResponse = authenticationService.getAccessToken(signInRequest);
        try {
            emailService.emailVerification(tokenResponse.getAccessToken(), req.getEmail(), req.getFirstName() + " " + req.getLastName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return UserCreationResponse.builder()
                .id(savedApplicant.getId())
                .firstName(savedApplicant.getFirstName())
                .lastName(savedApplicant.getLastName())
                .email(savedApplicant.getEmail())
                .userName(savedApplicant.getUsername())
                .userType(APPLICANT)
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserCreationResponse createEmployer(EmployerCreationRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByUserName(req.getUserName())) {
            throw new RuntimeException("Username already exists");
        }

        Employer employer = new Employer();
        employer.setId("EMP-" + UUID.randomUUID().toString());
        employer.setUserType(EMPLOYER);

        Role employerRole = roleRepository.findById(3L)
                .orElseThrow(() -> new RuntimeException("Role Employer not found"));
        employer.setRole(employerRole);

        employer.setFirstName(req.getFirstName());
        employer.setLastName(req.getLastName());
        employer.setEmail(req.getEmail());
        employer.setUserName(req.getUserName());
        employer.setPassword(passwordEncoder.encode(req.getPassword()));
        employer.setUserStatus(UserStatus.INACTIVE);

        employer.setPhone(req.getPhone());
        employer.setCompanyName(req.getCompanyName());
        employer.setIndustry(req.getIndustry());
        employer.setLocation(req.getAddress());

        Employer savedEmployer = userRepository.save(employer);

        SignInRequest signInRequest = SignInRequest.builder()
                .username(req.getUserName())
                .password(req.getPassword())
                .platform(req.getPlatform())
                .deviceToken(req.getDeviceToken())
                .versionApp(req.getVersionApp())
                .build();

        TokenResponse tokenResponse = authenticationService.getAccessToken(signInRequest);
        try {
            emailService.emailVerification(tokenResponse.getAccessToken(), req.getEmail(), req.getFirstName() + " " + req.getLastName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return UserCreationResponse.builder()
                .id(savedEmployer.getId())
                .firstName(savedEmployer.getFirstName())
                .lastName(savedEmployer.getLastName())
                .email(savedEmployer.getEmail())
                .userName(savedEmployer.getUsername())
                .userType(EMPLOYER)
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .build();
    }

    @Override
    public void changePassword(ChangePasswordRequest req) {
        log.info("Changing password for user: {}", req);

        User user = getUser(req.getId());

        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Mật khẩu cũ không trùng khớp");
        }

        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            throw new BadRequestException("Mật khẩu mới và xác nhận mật khẩu không khớp");
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);

        log.info("Changed password for user: {}", user);
    }

    public boolean verifySecretCode(String email,String secretCode) {
        String redisKey = "email_verification:" + email;
        String storedSecretCode = (String) redisTemplate.opsForValue().get(redisKey);
//        log.info("Code: {}",secretCode);
//        log.info("Secret code: {}",storedSecretCode);
        if (storedSecretCode == null) {
            return false;
        }else if(storedSecretCode.equals(secretCode)){
            User user = userRepository.findByEmail(email);
            user.setUserStatus(UserStatus.ACTIVE);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }
    private User getUser(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: "));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void requestActivation(String userId) {
        log.info("User {} requesting activation", userId);
        User user = getUser(userId);
        
        // Chỉ cho phép user INACTIVE request activation
        if (user.getUserStatus() != UserStatus.INACTIVE) {
            throw new RuntimeException("Chỉ tài khoản đang tạm khóa mới có thể yêu cầu kích hoạt");
        }
        
        // Lấy danh sách tất cả admin để gửi notification
        List<User> admins = userRepository.findByUserType(UserType.ADMIN);
        
        String userFullName = user.getFirstName() + " " + user.getLastName();
        String userTypeText = user.getUserType() == UserType.APPLICANT ? "Ứng viên" : "Nhà tuyển dụng";
        String message = String.format("%s %s (ID: %s) đã yêu cầu kích hoạt tài khoản",
                userTypeText, userFullName, userId);
        
        // Gửi notification cho tất cả admin
        for (User admin : admins) {
            notificationService.createSystemNotification(admin.getId(), message);
        }

        // Đồng thời gửi lại email kích hoạt tài khoản cho chính user
        // Tạo một token ngẫu nhiên để dùng làm secret code
        // Lưu ý: Không thể dùng access token vì user INACTIVE không thể đăng nhập
        String token = UUID.randomUUID().toString();
        try {
            emailService.emailVerification(token, user.getEmail(), userFullName);
            log.info("Resent activation email to user {} ({})", userId, user.getEmail());
        } catch (IOException e) {
            log.error("Failed to resend activation email to user {}: {}", userId, e.getMessage());
            // Ném lỗi để frontend biết việc gửi email thất bại
            throw new RuntimeException("Không thể gửi email kích hoạt. Vui lòng kiểm tra cấu hình email hoặc thử lại sau: " + e.getMessage());
        }

        log.info("Activation request sent to {} admins for user {}", admins.size(), userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveActivation(String userId) {
        log.info("Approving activation for user {}", userId);
        User user = getUser(userId);
        
        // Chỉ cho phép approve user INACTIVE
        if (user.getUserStatus() != UserStatus.INACTIVE) {
            throw new RuntimeException("Chỉ có thể kích hoạt tài khoản đang ở trạng thái tạm khóa");
        }
        
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        
        // Gửi notification cho user
        String message = "Tài khoản của bạn đã được kích hoạt thành công. Bạn có thể sử dụng đầy đủ các chức năng của hệ thống.";
        notificationService.createUserStatusNotification(userId, message);
        
        log.info("User {} has been activated successfully", userId);
    }
}
