package vn.iwork4se.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.iwork4se.common.UserStatus;
import vn.iwork4se.controller.request.ChangePasswordRequest;
import vn.iwork4se.controller.request.EmployerUpdateRequest;
import vn.iwork4se.controller.response.EmployerPageResponse;
import vn.iwork4se.controller.response.EmployerResponse;
import vn.iwork4se.exception.ResourceNotFoundException;
import vn.iwork4se.model.Employer;
import vn.iwork4se.repository.EmployerRepository;
import vn.iwork4se.repository.UserRepository;
import vn.iwork4se.service.EmailService;
import vn.iwork4se.service.EmployerService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployerServiceImpl implements EmployerService {

    private final EmployerRepository employerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


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

    @Override
    public EmployerResponse findEmployerById(String id) {
        log.info("Get employer detail by id: {}", id);
        Employer employer = getEmployerById(id);
        return EmployerResponse.builder()
                .firstName(employer.getFirstName())
                .lastName(employer.getLastName())
                .email(employer.getEmail())
                .address(employer.getAddress())
                .birthday(employer.getBirthday())
                .phone(employer.getPhone())
                .gender(employer.getGender())
                .companyName(employer.getCompanyName())
                .location(employer.getLocation())
                .industry(employer.getIndustry())
                .description(employer.getDescription())
                .logoUrl(employer.getLogoUrl())
                .build();
    }

    @Override
    public EmployerPageResponse findAllEmployers(String keyword, String sort, int page, int size) {
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
        if (StringUtils.hasLength(sort)) {
            Pattern pattern = Pattern.compile("^(\\w+)(:)(asc|desc)$");
            Matcher matcher = pattern.matcher(sort);
            if (matcher.find()) {
                String column = matcher.group(1);
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    order = new Sort.Order(Sort.Direction.ASC, column);
                } else {
                    order = new Sort.Order(Sort.Direction.DESC, column);
                }
            }
        }

        int pageNo = 0;
        if (page > 0) {
            pageNo = page - 1;
        }

        Pageable pageable = PageRequest.of(pageNo, size, Sort.by(order));
        Page<Employer> employerEntities = null;
        if (StringUtils.hasLength(keyword)) {
            employerEntities = employerRepository.searchByKeywords(keyword, pageable);
        } else {
            employerEntities = employerRepository.findAll(pageable);
        }

        return getEmployerPageResponse(page, size, employerEntities);
    }

    @Override
    public void deleteEmployerById(String id) {
        log.info("Deleting employer with id: {}", id);
        Employer employer = getEmployerById(id);
        employer.setUserStatus(UserStatus.DELETED);
        userRepository.save(employer);
        log.info("Deleted user: {}", employer);
    }

    private Employer getEmployerById(String id) {
        return employerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: "));
    }
    private static EmployerPageResponse getEmployerPageResponse(int page, int size, Page<Employer> employerEntities) {
        List<EmployerResponse> employerList = employerEntities.stream().map(
                employerEntity -> EmployerResponse.builder()
                        .firstName(employerEntity.getFirstName())
                        .lastName(employerEntity.getLastName())
                        .gender(employerEntity.getGender())
                        .birthday(employerEntity.getBirthday())
                        .email(employerEntity.getEmail())
                        .phone(employerEntity.getPhone())
                        .address(employerEntity.getAddress())
                        .companyName(employerEntity.getCompanyName())
                        .location(employerEntity.getLocation())
                        .industry(employerEntity.getIndustry())
                        .description(employerEntity.getDescription())
                        .logoUrl(employerEntity.getLogoUrl())
                        .build()
        ).toList();

        EmployerPageResponse employerPageResponse = new EmployerPageResponse();
        employerPageResponse.setPageNumber(page);
        employerPageResponse.setPageSize(size);
        employerPageResponse.setTotalPages(employerEntities.getTotalPages());
        employerPageResponse.setTotalElements(employerEntities.getTotalElements());
        employerPageResponse.setEmployers(employerList);
        return employerPageResponse;
    }
}
