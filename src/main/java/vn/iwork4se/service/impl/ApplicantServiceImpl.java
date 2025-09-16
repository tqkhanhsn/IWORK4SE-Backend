package vn.iwork4se.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.iwork4se.common.UserStatus;
import vn.iwork4se.controller.request.ApplicantCreationRequest;
import vn.iwork4se.controller.response.ApplicantCreationResponse;
import vn.iwork4se.model.Applicant;
import vn.iwork4se.repository.ApplicantRepository;
import vn.iwork4se.repository.UserRepository;
import vn.iwork4se.service.ApplicantService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicantServiceImpl implements ApplicantService {
    private final ApplicantRepository applicantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public ApplicantCreationResponse save(ApplicantCreationRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByUserName(req.getUserName())) {
            throw new RuntimeException("Username already exists");
        }

        Applicant applicant = new Applicant();
        applicant.setId("APP"+UUID.randomUUID().toString());
        applicant.setFirstName(req.getFirstName());
        applicant.setLastName(req.getLastName());
        applicant.setEmail(req.getEmail());
        applicant.setUserName(req.getUserName());
        applicant.setPassword(passwordEncoder.encode(req.getPassword()));


        Applicant savedApplicant = applicantRepository.save(applicant);

        return ApplicantCreationResponse.builder()
                .id(savedApplicant.getId())
                .firstName(savedApplicant.getFirstName())
                .lastName(savedApplicant.getLastName())
                .email(savedApplicant.getEmail())
                .userName(savedApplicant.getUserName())
                .build();
    }
}
