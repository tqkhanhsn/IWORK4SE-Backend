package vn.iwork4se.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.iwork4se.common.UserStatus;
import vn.iwork4se.controller.request.ChangePasswordRequest;
import vn.iwork4se.controller.request.EmployerCreationRequest;
import vn.iwork4se.controller.request.EmployerUpdateRequest;
import vn.iwork4se.controller.response.EmployerCreationResponse;
import vn.iwork4se.exception.ResourceNotFoundException;
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
        log.info("Creat user with request: {}", req);
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
        employer.setCreateAt(LocalDate.now());


        Employer savedEmployer = employerRepository.save(employer);

        return EmployerCreationResponse.builder()
                .id(savedEmployer.getId())
                .firstName(savedEmployer.getFirstName())
                .lastName(savedEmployer.getLastName())
                .email(savedEmployer.getEmail())
                .userName(savedEmployer.getUserName())
                .build();
    }

    @Override
    public void updateEmployer(EmployerUpdateRequest req) {
        log.info("Update employer with request: {}", req);
        Employer employer = getEmployerById(req.getId());
        employer.setFirstName(req.getFirstName());
        employer.setLastName(req.getLastName());
        employer.setAddress(req.getAddress());
        employer.setBirthday(req.getBirthday());
        employer.setPhone(req.getPhone());
        employer.setGender(req.getGender());
        employer.setUpdateAt(LocalDate.now());
        employer.setCompanyName(req.getCompanyName());
        employer.setLocation(req.getLocation());
        employer.setIndustry(req.getIndustry());
        employer.setDescription(req.getDescription());
        employer.setLogoUrl(req.getLogoUrl());
        employerRepository.save(employer);

    }

    @Override
    public void changePasswordEmployer(ChangePasswordRequest req) {
        log.info("Changing password for user with request: {}", req);
        Employer employer = getEmployerById(req.getId());
        if(req.getPassword().equals(req.getConfirmPassword())) {
            employer.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        employerRepository.save(employer);
        log.info("Change password user: {}", employer);
    }

    private Employer getEmployerById(String id) {
        return employerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: "));
    }
}
