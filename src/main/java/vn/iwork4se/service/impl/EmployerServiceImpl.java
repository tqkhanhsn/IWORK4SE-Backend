package vn.iwork4se.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.iwork4se.common.UserStatus;
import vn.iwork4se.controller.request.EmployerCreationRequest;
import vn.iwork4se.controller.response.EmployerCreationResponse;
import vn.iwork4se.model.Employer;
import vn.iwork4se.repository.EmployerRepository;
import vn.iwork4se.repository.UserRepository;
import vn.iwork4se.service.EmployerService;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployerServiceImpl implements EmployerService {
    private final EmployerRepository employerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public EmployerCreationResponse save(EmployerCreationRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByUserName(req.getUserName())) {
            throw new RuntimeException("Username already exists");
        }

        Employer employer = new Employer();
        employer.setId("EMP-" + UUID.randomUUID().toString());
        employer.setFirstName(req.getFirstName());
        employer.setLastName(req.getLastName());
        employer.setEmail(req.getEmail());
        employer.setUserName(req.getUserName());
        employer.setPassword(passwordEncoder.encode(req.getPassword()));


        Employer savedEmployer = employerRepository.save(employer);

        return EmployerCreationResponse.builder()
                .id(savedEmployer.getId())
                .firstName(savedEmployer.getFirstName())
                .lastName(savedEmployer.getLastName())
                .email(savedEmployer.getEmail())
                .userName(savedEmployer.getUserName())
                .build();
    }
}
