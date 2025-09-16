package vn.iwork4se.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.iwork4se.common.Gender;
import vn.iwork4se.common.UserStatus;
import vn.iwork4se.controller.request.ApplicantCreationRequest;
import vn.iwork4se.controller.request.ApplicantUpdateRequest;
import vn.iwork4se.controller.response.ApplicantCreationResponse;
import vn.iwork4se.exception.ResourceNotFoundException;
import vn.iwork4se.model.Applicant;
import vn.iwork4se.model.Certificate;
import vn.iwork4se.model.Employer;
import vn.iwork4se.repository.ApplicantRepository;
import vn.iwork4se.repository.CertificateRepository;
import vn.iwork4se.repository.UserRepository;
import vn.iwork4se.service.ApplicantService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicantServiceImpl implements ApplicantService {
    private final ApplicantRepository applicantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CertificateRepository certificateRepository;

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

    private Applicant getApplicantById(String id) {
        return applicantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: "));
    }


}
