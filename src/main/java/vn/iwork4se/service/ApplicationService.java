package vn.iwork4se.service;

import vn.iwork4se.common.ApplicationStatus;
import vn.iwork4se.controller.request.ApplicationCreationRequest;
import vn.iwork4se.controller.request.ApplicationUpdateRequest;
import vn.iwork4se.controller.response.ApplicationCreationResponse;
import vn.iwork4se.controller.response.ApplicationPageResponse;
import vn.iwork4se.controller.response.ApplicationResponse;
import vn.iwork4se.model.Application;

import java.time.LocalDateTime;

public interface ApplicationService {
    // CRUD methods for Application entity
    ApplicationCreationResponse save(ApplicationCreationRequest request);
    void updateApplication(ApplicationUpdateRequest request);
    void deleteApplication(String id);
    Application getApplicationById(String id);
    ApplicationResponse findApplicationById(String id);
    
    // Business methods
    ApplicationPageResponse findApplicationsByApplicant(String applicantId, int page, int size);
    ApplicationPageResponse findApplicationsByJob(String jobId, int page, int size);
    ApplicationPageResponse findApplicationsByStatus(ApplicationStatus status, int page, int size);
    ApplicationPageResponse findApplicationsByApplicantAndStatus(String applicantId, ApplicationStatus status, int page, int size);
    ApplicationPageResponse findApplicationsByJobAndStatus(String jobId, ApplicationStatus status, int page, int size);
    ApplicationPageResponse findApplicationsByEmployer(String employerId, ApplicationStatus status, int page, int size);
    ApplicationPageResponse findApplicationsByDateRange(LocalDateTime startDate, LocalDateTime endDate, int page, int size);
    ApplicationPageResponse findRecentApplications(int page, int size);
    ApplicationPageResponse findApplicationsByMultipleCriteria(String applicantId, String jobId, ApplicationStatus status, 
                                                             LocalDateTime startDate, LocalDateTime endDate, int page, int size);
    
    // Status management
    void updateApplicationStatus(String id, ApplicationStatus status);
    void approveApplication(String id);
    void rejectApplication(String id);
    void withdrawApplication(String id);
    
    // Utility methods
    boolean hasApplicantAppliedForJob(String applicantId, String jobId);
    long countApplicationsByApplicant(String applicantId);
    long countApplicationsByJob(String jobId);
    long countApplicationsByStatus(ApplicationStatus status);
}
