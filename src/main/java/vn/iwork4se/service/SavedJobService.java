package vn.iwork4se.service;

import vn.iwork4se.controller.request.SavedJobCreationRequest;
import vn.iwork4se.controller.response.SavedJobCreationResponse;
import vn.iwork4se.controller.response.SavedJobPageResponse;
import vn.iwork4se.controller.response.SavedJobResponse;
import vn.iwork4se.model.SavedJob;

import java.time.LocalDate;

public interface SavedJobService {
    // CRUD methods for SavedJob entity
    SavedJobCreationResponse save(SavedJobCreationRequest request);
    void deleteSavedJob(String id);
    void deleteSavedJobByApplicantAndJob(String applicantId, String jobId);
    SavedJob getSavedJobById(String id);
    SavedJobResponse findSavedJobById(String id);
    
    // Business methods
    SavedJobPageResponse findSavedJobsByApplicant(String applicantId, int page, int size);
    SavedJobPageResponse findSavedJobsByJob(String jobId, int page, int size);
    SavedJobPageResponse findSavedJobsByApplicantAndDateRange(String applicantId, LocalDate startDate, LocalDate endDate, int page, int size);
    SavedJobPageResponse findRecentSavedJobsByApplicant(String applicantId, int page, int size);
    
    // Utility methods
    boolean isJobSavedByApplicant(String applicantId, String jobId);
    long countSavedJobsByApplicant(String applicantId);
    long countSavedJobsByJob(String jobId);
    
    // Toggle save/unsave job
    SavedJobCreationResponse toggleSaveJob(String applicantId, String jobId);
}
