package vn.iwork4se.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.iwork4se.common.Gender;
import vn.iwork4se.common.UserStatus;
import vn.iwork4se.controller.request.ApplicantCreationRequest;
import vn.iwork4se.controller.request.ApplicantUpdateRequest;
import vn.iwork4se.controller.request.CertificateRequest;
import vn.iwork4se.controller.request.ChangePasswordRequest;
import vn.iwork4se.controller.response.*;
import vn.iwork4se.exception.ResourceNotFoundException;
import vn.iwork4se.model.Applicant;
import vn.iwork4se.model.Certificate;
import vn.iwork4se.model.Employer;
import vn.iwork4se.repository.ApplicantRepository;
import vn.iwork4se.repository.CertificateRepository;
import vn.iwork4se.repository.EmployerRepository;
import vn.iwork4se.repository.UserRepository;
import vn.iwork4se.service.ApplicantService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicantServiceImpl implements ApplicantService {

    private final ApplicantRepository applicantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CertificateRepository certificateRepository;
    private final EmployerRepository employerRepository;

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
        applicant.setCreateAt(LocalDate.now());
        applicant.setUserStatus(UserStatus.INACTIVE);

        Applicant savedApplicant = applicantRepository.save(applicant);

        return ApplicantCreationResponse.builder()
                .id(savedApplicant.getId())
                .firstName(savedApplicant.getFirstName())
                .lastName(savedApplicant.getLastName())
                .email(savedApplicant.getEmail())
                .userName(savedApplicant.getUserName())
                .build();
    }

    @Override
    public void updateApplicant(ApplicantUpdateRequest req) {
        log.info("Update Applicant with request: {}", req);

        Applicant applicant = getApplicantById(req.getId());


        applicant.setFirstName(req.getFirstName());
        applicant.setLastName(req.getLastName());
        applicant.setEmail(req.getEmail());
        applicant.setAddress(req.getAddress());
        applicant.setBirthday(req.getBirthday());
        applicant.setPhone(req.getPhone());
        applicant.setGender(req.getGender());
        applicant.setYearsOfExperience(req.getYearsOfExperience());
        applicant.setCareerObjective(req.getCareerObjective());
        applicant.setUniversityName(req.getUniversityName());
        applicant.setGpa(req.getGpa());
        applicant.setMajor(req.getMajor());

        applicant.getSkills().clear();
        if (req.getSkills() != null) {
            applicant.getSkills().addAll(req.getSkills());
        }

        List<Certificate> certificates = new ArrayList<>();

        if (req.getCertificates() != null) {
            req.getCertificates().forEach(certReq -> {
                Certificate certificate = new Certificate();
                certificate.setCertificateName(certReq.getCertificateName());
                certificate.setIssuingOrganization(certReq.getIssuingOrganization());
                certificate.setIssueDate(certReq.getIssueDate());
                certificate.setExpirationDate(certReq.getExpirationDate());
                certificate.setCertificateId(certReq.getCertificateId());
                certificate.setCertificateUrl(certReq.getCertificateUrl());
                certificate.setNotes(certReq.getNotes());
                certificate.setApplicant(applicant);
                certificates.add(certificate);
            });

            if(applicant.getCertificates().size()>0){
                certificateRepository.deleteByApplicantId(applicant.getId());
            }
            certificateRepository.saveAll(certificates);
            log.info("Updated certificates: {}", certificates);
        }

        applicantRepository.save(applicant);
        log.info("Updated applicant: {}", applicant);


    }

    @Override
    public void changePasswordApplicant(ChangePasswordRequest req) {
        log.info("Changing password for user with request: {}", req);
        Applicant applicant = getApplicantById(req.getId());
        if(req.getPassword().equals(req.getConfirmPassword())) {
            applicant.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        applicantRepository.save(applicant);
        log.info("Change password user: {}", applicant);

    }

    @Override
    public ApplicantResponse findApplicantById(String id) {
        log.info("Get employer detail by id: {}", id);
        Applicant applicant = getApplicantById(id);
        return ApplicantResponse.builder()
                .firstName(applicant.getFirstName())
                .lastName(applicant.getLastName())
                .email(applicant.getEmail())
                .address(applicant.getAddress())
                .birthday(applicant.getBirthday())
                .phone(applicant.getPhone())
                .gender(applicant.getGender())
                .yearsOfExperience(applicant.getYearsOfExperience())
                .careerObjective(applicant.getCareerObjective())
                .universityName(applicant.getUniversityName())
                .gpa(applicant.getGpa())
                .major(applicant.getMajor())
                .certificates(applicant.getCertificates().stream().map(cert -> CertificateResponse.builder()
                        .certificateName(cert.getCertificateName())
                        .issuingOrganization(cert.getIssuingOrganization())
                        .issueDate(cert.getIssueDate())
                        .expirationDate(cert.getExpirationDate())
                        .certificateId(cert.getCertificateId())
                        .certificateUrl(cert.getCertificateUrl())
                        .notes(cert.getNotes())
                        .build()).collect(Collectors.toList()))
                .Skills(new ArrayList<>(applicant.getSkills()))
                .build();





    }

    @Override
    public ApplicantPageResponse findAllApplicants(String keyword, String sort, int page, int size) {
        log.info("Finding all employers with keyword: {}, sort: {}, page: {}, size: {}", keyword, sort, page, size);

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
        Page<Applicant> applicantEntities = null;
        if (StringUtils.hasLength(keyword)) {
            applicantEntities = applicantRepository.searchByKeywords(keyword, pageable);
        } else {
            applicantEntities = applicantRepository.findAll(pageable);
        }

        return getApplicantPageResponse(page, size, applicantEntities);
    }

    @Override
    public void deleteApplicantById(String id) {
        log.info("Deleting applicant with id: {}", id);
        Applicant applicant = getApplicantById(id);
        applicant.setUserStatus(UserStatus.DELETED);
        userRepository.save(applicant);
        log.info("Deleted user: {}", applicant);
    }

    private Applicant getApplicantById(String id) {
        return applicantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: "));
    }

    private static ApplicantPageResponse getApplicantPageResponse(int page, int size, Page<Applicant> applicantEntities) {
        List<ApplicantResponse> applicantList = applicantEntities.stream().map(
                applicantEntity -> ApplicantResponse.builder()
                        .firstName(applicantEntity.getFirstName())
                        .lastName(applicantEntity.getLastName())
                        .gender(applicantEntity.getGender())
                        .birthday(applicantEntity.getBirthday())
                        .email(applicantEntity.getEmail())
                        .phone(applicantEntity.getPhone())
                        .address(applicantEntity.getAddress())
                        .yearsOfExperience(applicantEntity.getYearsOfExperience())
                        .careerObjective(applicantEntity.getCareerObjective())
                        .universityName(applicantEntity.getUniversityName())
                        .gpa(applicantEntity.getGpa())
                        .major(applicantEntity.getMajor())
                        .certificates(applicantEntity.getCertificates().stream().map(cert ->
                                CertificateResponse.builder()
                                        .certificateName(cert.getCertificateName())
                                        .issuingOrganization(cert.getIssuingOrganization())
                                        .issueDate(cert.getIssueDate())
                                        .expirationDate(cert.getExpirationDate())
                                        .certificateId(cert.getCertificateId())
                                        .certificateUrl(cert.getCertificateUrl())
                                        .notes(cert.getNotes())
                                        .build()).collect(Collectors.toList()))
                        .Skills(new ArrayList<>(applicantEntity.getSkills()))
                        .build()
        ).toList();

        ApplicantPageResponse applicantPageResponse = new ApplicantPageResponse();
        applicantPageResponse.setPageNumber(page);
        applicantPageResponse.setPageSize(size);
        applicantPageResponse.setTotalPages(applicantEntities.getTotalPages());
        applicantPageResponse.setTotalElements(applicantEntities.getTotalElements());
        applicantPageResponse.setApplicants(applicantList);
        return applicantPageResponse;
    }


}
