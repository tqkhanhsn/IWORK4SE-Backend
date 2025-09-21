package vn.iwork4se.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iwork4se.common.UserStatus;
import vn.iwork4se.controller.request.UserCreationRequest;
import vn.iwork4se.controller.response.UserCreationResponse;
import vn.iwork4se.model.Applicant;
import vn.iwork4se.model.User;
import vn.iwork4se.repository.UserRepository;
import vn.iwork4se.service.EmailService;
import vn.iwork4se.service.UserService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserCreationResponse createUser(UserCreationRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByUserName(req.getUserName())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new Applicant();
        user.setId("APP"+ UUID.randomUUID().toString());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setEmail(req.getEmail());
        user.setUserName(req.getUserName());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setCreateAt(LocalDate.now());
        user.setUserStatus(UserStatus.INACTIVE);

        User savedUser = userRepository.save(user);
        try {
            emailService.emailVerification(req.getEmail(),req.getFirstName()+ req.getLastName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return UserCreationResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .userName(user.getUsername())
                .build();
    }
}
