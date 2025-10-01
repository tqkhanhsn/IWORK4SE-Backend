package vn.iwork4se.service;

import vn.iwork4se.controller.request.CVCreationRequest;
import vn.iwork4se.controller.request.CVUpdateRequest;
import vn.iwork4se.controller.response.CVCreationResponse;
import vn.iwork4se.controller.response.CVPageResponse;
import vn.iwork4se.controller.response.CVResponse;
import vn.iwork4se.model.CV;

import java.time.LocalDate;
import java.util.List;

public interface CVService {
    // CRUD methods for CV entity
    CVCreationResponse save(CVCreationRequest request);
    void updateCV(CVUpdateRequest request);
    void deleteCV(String id);
    CV getCVById(String id);
    CVResponse findCVById(String id);
    
    // Business methods
    CVPageResponse findCVsByApplicant(String applicantId, int page, int size);
    CVPageResponse findCVsByApplicantOrderByDate(String applicantId, int page, int size);
    CVPageResponse findCVsByDateRange(LocalDate startDate, LocalDate endDate, int page, int size);
    CVPageResponse findCVsByApplicantAndDateRange(String applicantId, LocalDate startDate, LocalDate endDate, int page, int size);
    CVPageResponse findCVsUsedInApplications(int page, int size);
    CVPageResponse findRecentCVs(int page, int size);
    
    // Utility methods
    CVResponse findLatestCVByApplicant(String applicantId);
    List<CVResponse> findUnusedCVsByApplicant(String applicantId);
    long countCVsByApplicant(String applicantId);
    
    // File management
    List<CVResponse> findCVsByUrlPattern(String urlPattern);
    void deleteCVsByUrlPattern(String urlPattern);
    
    // CV validation
    boolean isValidCVUrl(String url);
    boolean isCVOwnedByApplicant(String cvId, String applicantId);
}
