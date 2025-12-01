package vn.iwork4se.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.iwork4se.common.ApplicationStatus;
import vn.iwork4se.controller.request.ApplicationCreationRequest;
import vn.iwork4se.controller.request.ApplicationUpdateRequest;
import vn.iwork4se.controller.response.ApplicationCreationResponse;
import vn.iwork4se.controller.response.ApplicationPageResponse;
import vn.iwork4se.controller.response.ApplicationResponse;
import vn.iwork4se.event.ApplicationStatusChangedEvent;
import vn.iwork4se.exception.ResourceNotFoundException;
import vn.iwork4se.model.Applicant;
import vn.iwork4se.model.Application;
import vn.iwork4se.model.CV;
import vn.iwork4se.model.JobPost;
import vn.iwork4se.repository.ApplicantRepository;
import vn.iwork4se.repository.ApplicationRepository;
import vn.iwork4se.repository.CVRepository;
import vn.iwork4se.repository.JobPostRepository;
import vn.iwork4se.service.ApplicationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static vn.iwork4se.common.ApplicationStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final ApplicantRepository applicantRepository;
    private final JobPostRepository jobPostRepository;
    private final CVRepository cvRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public ApplicationCreationResponse save(ApplicationCreationRequest request) {
        // Check if applicant already applied for this job
        Optional<Application> existingApplication = applicationRepository.findByApplicantIdAndJobId(
                request.getApplicantId(), request.getJobId());

        if (existingApplication.isPresent()) {
            throw new RuntimeException("Applicant has already applied for this job");
        }

        Applicant applicant = applicantRepository.findById(request.getApplicantId())
                .orElseThrow(() -> new RuntimeException("Applicant not found"));

        // Kiểm tra trạng thái applicant - chỉ cho phép ACTIVE apply
        if (applicant.getUserStatus() == null || applicant.getUserStatus() != vn.iwork4se.common.UserStatus.ACTIVE) {
            if (applicant.getUserStatus() == vn.iwork4se.common.UserStatus.INACTIVE) {
                throw new RuntimeException("Tài khoản của bạn đang bị tạm khóa. Vui lòng kích hoạt tài khoản để tiếp tục sử dụng dịch vụ.");
            }
            throw new RuntimeException("Bạn không thể ứng tuyển với trạng thái tài khoản hiện tại");
        }

        JobPost jobPost = jobPostRepository.findById(request.getJobId())
                .orElseThrow(() -> new RuntimeException("Job post not found"));

        // Chỉ cho phép apply vào các job đã được duyệt và chưa hết hạn
        if (jobPost.getJobStatus() == null || jobPost.getJobStatus() != vn.iwork4se.common.JobStatus.ACCEPTED) {
            throw new RuntimeException("Cannot apply for a job that is not in ACCEPTED status");
        }
        LocalDate today = LocalDate.now();
        if (jobPost.getClosingDate() != null && jobPost.getClosingDate().isBefore(today)) {
            throw new RuntimeException("Cannot apply for an expired job");
        }

        CV cv = null;
        if (request.getCvId() != null) {
            cv = cvRepository.findById(request.getCvId())
                    .orElseThrow(() -> new RuntimeException("CV not found"));
        }

        Application application = Application.builder()
                .id("APP" + UUID.randomUUID().toString())
                .applicant(applicant)
                .job(jobPost)
                .cv(cv)
                .appliedAt(LocalDateTime.now())
                .applicationStatus(ApplicationStatus.PENDING)
                .updateAt(LocalDateTime.now())
                .build();

        Application savedApplication = applicationRepository.save(application);
        log.info("Application created successfully, applicationId={}", savedApplication.getId());

        return ApplicationCreationResponse.builder()
                .id(savedApplication.getId())
                .applicantId(savedApplication.getApplicant().getId())
                .jobId(savedApplication.getJob().getId())
                .cvId(savedApplication.getCv() != null ? savedApplication.getCv().getId() : null)
                .appliedAt(savedApplication.getAppliedAt())
                .applicationStatus(savedApplication.getApplicationStatus())
                .build();
    }

    @Override
    public void updateApplication(ApplicationUpdateRequest request) {
        Application application = getApplicationById(request.getId());

        if (request.getCvId() != null) {
            CV cv = cvRepository.findById(request.getCvId())
                    .orElseThrow(() -> new RuntimeException("CV not found"));
            application.setCv(cv);
        }

        if (request.getApplicationStatus() != null) {
            application.setApplicationStatus(request.getApplicationStatus());
        }

        application.setUpdateAt(LocalDateTime.now());
        applicationRepository.save(application);
        log.info("Application updated successfully, applicationId={}", application.getId());
    }

    @Override
    public void deleteApplication(String id) {
        Application application = getApplicationById(id);
        applicationRepository.delete(application);
        log.info("Application deleted successfully, applicationId={}", id);
    }

    @Override
    public Application getApplicationById(String id) {
        return applicationRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Application not found with id: " + id));
    }

    @Override
    public ApplicationResponse findApplicationById(String id) {
        Application application = getApplicationById(id);
        return convertToApplicationResponse(application);
    }

    @Override
    public ApplicationPageResponse findApplicationsByApplicant(String applicantId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "appliedAt"));
        Page<Application> applicationPage = applicationRepository.findByApplicantId(applicantId, pageable);

        List<ApplicationResponse> applicationResponses = applicationPage.getContent().stream()
                .map(this::convertToApplicationResponse)
                .collect(Collectors.toList());

        return new ApplicationPageResponse(
                applicationResponses,
                applicationPage.getNumber(),
                applicationPage.getSize(),
                applicationPage.getTotalPages(),
                applicationPage.getTotalElements()
        );
    }

    @Override
    public ApplicationPageResponse findApplicationsByJob(String jobId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "appliedAt"));
        Page<Application> applicationPage = applicationRepository.findByJobId(jobId, pageable);

        List<ApplicationResponse> applicationResponses = applicationPage.getContent().stream()
                .map(this::convertToApplicationResponse)
                .collect(Collectors.toList());

        return new ApplicationPageResponse(
                applicationResponses,
                applicationPage.getNumber(),
                applicationPage.getSize(),
                applicationPage.getTotalPages(),
                applicationPage.getTotalElements()
        );
    }

    @Override
    public ApplicationPageResponse findApplicationsByStatus(ApplicationStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "appliedAt"));
        Page<Application> applicationPage = applicationRepository.findByApplicationStatus(status, pageable);

        List<ApplicationResponse> applicationResponses = applicationPage.getContent().stream()
                .map(this::convertToApplicationResponse)
                .collect(Collectors.toList());

        return new ApplicationPageResponse(
                applicationResponses,
                applicationPage.getNumber(),
                applicationPage.getSize(),
                applicationPage.getTotalPages(),
                applicationPage.getTotalElements()
        );
    }

    @Override
    public ApplicationPageResponse findApplicationsByApplicantAndStatus(String applicantId, ApplicationStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "appliedAt"));
        Page<Application> applicationPage = applicationRepository.findByApplicantIdAndApplicationStatus(applicantId, status, pageable);

        List<ApplicationResponse> applicationResponses = applicationPage.getContent().stream()
                .map(this::convertToApplicationResponse)
                .collect(Collectors.toList());

        return new ApplicationPageResponse(
                applicationResponses,
                applicationPage.getNumber(),
                applicationPage.getSize(),
                applicationPage.getTotalPages(),
                applicationPage.getTotalElements()
        );
    }

    @Override
    public ApplicationPageResponse findApplicationsByJobAndStatus(String jobId, ApplicationStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "appliedAt"));
        Page<Application> applicationPage = applicationRepository.findByJobIdAndApplicationStatus(jobId, status, pageable);

        List<ApplicationResponse> applicationResponses = applicationPage.getContent().stream()
                .map(this::convertToApplicationResponse)
                .collect(Collectors.toList());

        return new ApplicationPageResponse(
                applicationResponses,
                applicationPage.getNumber(),
                applicationPage.getSize(),
                applicationPage.getTotalPages(),
                applicationPage.getTotalElements()
        );
    }

    @Override
    public ApplicationPageResponse findApplicationsByEmployer(String employerId, ApplicationStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "appliedAt"));
        Page<Application> applicationPage = status == null
                ? applicationRepository.findByEmployerId(employerId, pageable)
                : applicationRepository.findByEmployerIdAndStatus(employerId, status, pageable);

        List<ApplicationResponse> applicationResponses = applicationPage.getContent().stream()
                .map(this::convertToApplicationResponse)
                .collect(Collectors.toList());

        return new ApplicationPageResponse(
                applicationResponses,
                applicationPage.getNumber(),
                applicationPage.getSize(),
                applicationPage.getTotalPages(),
                applicationPage.getTotalElements()
        );
    }

    @Override
    public ApplicationPageResponse findApplicationsByDateRange(LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "appliedAt"));
        Page<Application> applicationPage = applicationRepository.findByDateRange(startDate, endDate, pageable);

        List<ApplicationResponse> applicationResponses = applicationPage.getContent().stream()
                .map(this::convertToApplicationResponse)
                .collect(Collectors.toList());

        return new ApplicationPageResponse(
                applicationResponses,
                applicationPage.getNumber(),
                applicationPage.getSize(),
                applicationPage.getTotalPages(),
                applicationPage.getTotalElements()
        );
    }

    @Override
    public ApplicationPageResponse findRecentApplications(int page, int size) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "appliedAt"));
        Page<Application> applicationPage = applicationRepository.findRecentApplications(thirtyDaysAgo, pageable);

        List<ApplicationResponse> applicationResponses = applicationPage.getContent().stream()
                .map(this::convertToApplicationResponse)
                .collect(Collectors.toList());

        return new ApplicationPageResponse(
                applicationResponses,
                applicationPage.getNumber(),
                applicationPage.getSize(),
                applicationPage.getTotalPages(),
                applicationPage.getTotalElements()
        );
    }

    @Override
    public ApplicationPageResponse findApplicationsByMultipleCriteria(String applicantId, String jobId, ApplicationStatus status,
                                                                      LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "appliedAt"));
        Page<Application> applicationPage = applicationRepository.findByMultipleCriteria(
                applicantId, jobId, status, startDate, endDate, pageable);

        List<ApplicationResponse> applicationResponses = applicationPage.getContent().stream()
                .map(this::convertToApplicationResponse)
                .collect(Collectors.toList());

        return new ApplicationPageResponse(
                applicationResponses,
                applicationPage.getNumber(),
                applicationPage.getSize(),
                applicationPage.getTotalPages(),
                applicationPage.getTotalElements()
        );
    }

    private boolean isValidTransition(ApplicationStatus from, ApplicationStatus to) {

        switch (from) {
            case PENDING: return to == APPROVED || to == REJECTED || to == WITHDRAWN;
            case APPROVED: return to == WITHDRAWN;
            case REJECTED: return false; // đã reject thì không đổi nữa
            case WITHDRAWN: return false;
            default: return false;
        }
    }

    @Override
    public void updateApplicationStatus(String id, ApplicationStatus status) {
        Application application = getApplicationById(id);

        if (!isValidTransition(application.getApplicationStatus(), status)) {
            throw new IllegalStateException("Invalid status transition: "
                    + application.getApplicationStatus() + " -> " + status);
        }

        ApplicationStatus oldStatus = application.getApplicationStatus();
        application.setApplicationStatus(status);
        application.setUpdateAt(LocalDateTime.now());
        Application updatedApplication = applicationRepository.save(application);

        eventPublisher.publishEvent(new ApplicationStatusChangedEvent(
                this, updatedApplication, status, oldStatus
        ));

        log.info("Application status updated successfully, applicationId={}, newStatus={}", id, status);
    }

    @Override
    public void approveApplication(String id) {
        updateApplicationStatus(id, APPROVED);
    }

    @Override
    public void rejectApplication(String id) {
        updateApplicationStatus(id, REJECTED);
    }

    @Override
    public void withdrawApplication(String id) {
        updateApplicationStatus(id, WITHDRAWN);
    }

    @Override
    public boolean hasApplicantAppliedForJob(String applicantId, String jobId) {
        return applicationRepository.findByApplicantIdAndJobId(applicantId, jobId).isPresent();
    }

    @Override
    public long countApplicationsByApplicant(String applicantId) {
        return applicationRepository.countByApplicantId(applicantId);
    }

    @Override
    public long countApplicationsByJob(String jobId) {
        return applicationRepository.countByJobId(jobId);
    }

    @Override
    public long countApplicationsByStatus(ApplicationStatus status) {
        return applicationRepository.countByApplicationStatus(status);
    }

    private ApplicationResponse convertToApplicationResponse(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .applicantId(application.getApplicant().getId())
                .applicantName(application.getApplicant().getFirstName() + " " + application.getApplicant().getLastName())
                .jobId(application.getJob().getId())
                .jobTitle(application.getJob().getTitle())
                .jobPosition(application.getJob().getJobPosition())
                .logoUrl(application.getJob().getEmployer().getLogoUrl())
                .companyName(application.getJob().getEmployer() != null ? application.getJob().getEmployer().getCompanyName() : null)
                .location(application.getJob().getEmployer().getLocation())
                .minSalary(application.getJob().getMinSalary())
                .maxSalary(application.getJob().getMaxSalary())
                .closingDate(application.getJob().getClosingDate())
                .appliedDate(application.getAppliedAt())
                .status(application.getApplicationStatus())
                .cvFileName(application.getCv() != null ? application.getCv().getFileName() : null)
                .cvId(application.getCv() != null ? application.getCv().getId() : null)
                .cvUrl(application.getCv() != null ? application.getCv().getUrl() : null)
                .updateAt(application.getUpdateAt())
                .build();
    }

}
