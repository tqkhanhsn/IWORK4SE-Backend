package vn.iwork4se.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.iwork4se.controller.request.CVCreationRequest;
import vn.iwork4se.controller.request.CVUpdateRequest;
import vn.iwork4se.controller.response.CVCreationResponse;
import vn.iwork4se.controller.response.CVPageResponse;
import vn.iwork4se.controller.response.CVResponse;
import vn.iwork4se.exception.ResourceNotFoundException;
import vn.iwork4se.model.Applicant;
import vn.iwork4se.model.Application;
import vn.iwork4se.model.CV;
import vn.iwork4se.repository.ApplicantRepository;
import vn.iwork4se.repository.CVRepository;
import vn.iwork4se.service.CVService;
import vn.iwork4se.service.SupabaseStorageService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CVServiceImpl implements CVService {
    private final CVRepository cvRepository;
    private final ApplicantRepository applicantRepository;
    private final SupabaseStorageService supabaseStorageService;

    @Override
    public CVCreationResponse uploadCV(MultipartFile file, String applicantId, String fileName) {
        // Validate applicant exists
        Applicant applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new ResourceNotFoundException("Applicant not found with id: " + applicantId));


        String fileUrl = supabaseStorageService.uploadFile(file, applicantId);

        // Create CV record with the file URL
        CV cv = CV.builder()
                .id("CV" + UUID.randomUUID().toString())
                .url(fileUrl)
                .fileName(fileName)
                .uploadedDate(LocalDateTime.now())
                .applicant(applicant)
                .build();

        CV savedCV = cvRepository.save(cv);
        log.info("CV uploaded and saved successfully, cvId={}, url={}", savedCV.getId(), fileUrl);

        return CVCreationResponse.builder()
                .id(savedCV.getId())
                .url(savedCV.getUrl())
                .uploadedDate(savedCV.getUploadedDate())
                .applicantId(savedCV.getApplicant().getId())
                .build();
    }

    @Override
    public CVCreationResponse save(CVCreationRequest request) {
        Applicant applicant = applicantRepository.findById(request.getApplicantId())
                .orElseThrow(() -> new RuntimeException("Applicant not found"));

        CV cv = CV.builder()
                .id("CV" + UUID.randomUUID().toString())
                .url(request.getUrl())
                .uploadedDate(LocalDateTime.now())
                .applicant(applicant)
                .build();

        CV savedCV = cvRepository.save(cv);
        log.info("CV created successfully, cvId={}", savedCV.getId());

        return CVCreationResponse.builder()
                .id(savedCV.getId())
                .url(savedCV.getUrl())
                .uploadedDate(savedCV.getUploadedDate())
                .applicantId(savedCV.getApplicant().getId())
                .build();
    }

    @Override
    public void updateCV(CVUpdateRequest request) {
        CV cv = getCVById(request.getId());

        if (request.getUrl() != null) {
            cv.setUrl(request.getUrl());
        }

        cvRepository.save(cv);
        log.info("CV updated successfully, cvId={}", cv.getId());
    }

    @Override
    public void deleteCV(String id) {
        CV cv = getCVById(id);
        cvRepository.delete(cv);
        log.info("CV deleted successfully, cvId={}", id);
    }

    @Override
    public CV getCVById(String id) {
        return cvRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("CV not found with id: " + id));
    }

    @Override
    public CVResponse findCVById(String id) {
        CV cv = getCVById(id);
        return convertToCVResponse(cv);
    }

    @Override
    public CVPageResponse findCVsByApplicant(String applicantId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadedDate"));
        Page<CV> cvPage = cvRepository.findByApplicantId(applicantId, pageable);

        List<CVResponse> cvResponses = cvPage.getContent().stream()
                .map(this::convertToCVResponse)
                .collect(Collectors.toList());

        return new CVPageResponse(
                cvResponses,
                cvPage.getNumber(),
                cvPage.getSize(),
                cvPage.getTotalPages(),
                cvPage.getTotalElements()
        );
    }

    @Override
    public CVPageResponse findCVsByApplicantOrderByDate(String applicantId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadedDate"));
        Page<CV> cvPage = cvRepository.findByApplicantIdOrderByUploadedDateDesc(applicantId, pageable);

        List<CVResponse> cvResponses = cvPage.getContent().stream()
                .map(this::convertToCVResponse)
                .collect(Collectors.toList());

        return new CVPageResponse(
                cvResponses,
                cvPage.getNumber(),
                cvPage.getSize(),
                cvPage.getTotalPages(),
                cvPage.getTotalElements()
        );
    }

    @Override
    public CVPageResponse findCVsByDateRange(LocalDate startDate, LocalDate endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadedDate"));
        Page<CV> cvPage = cvRepository.findByDateRange(startDate, endDate, pageable);

        List<CVResponse> cvResponses = cvPage.getContent().stream()
                .map(this::convertToCVResponse)
                .collect(Collectors.toList());

        return new CVPageResponse(
                cvResponses,
                cvPage.getNumber(),
                cvPage.getSize(),
                cvPage.getTotalPages(),
                cvPage.getTotalElements()
        );
    }

    @Override
    public CVPageResponse findCVsByApplicantAndDateRange(String applicantId, LocalDate startDate, LocalDate endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadedDate"));
        Page<CV> cvPage = cvRepository.findByApplicantIdAndDateRange(applicantId, startDate, endDate, pageable);

        List<CVResponse> cvResponses = cvPage.getContent().stream()
                .map(this::convertToCVResponse)
                .collect(Collectors.toList());

        return new CVPageResponse(
                cvResponses,
                cvPage.getNumber(),
                cvPage.getSize(),
                cvPage.getTotalPages(),
                cvPage.getTotalElements()
        );
    }

    @Override
    public CVPageResponse findCVsUsedInApplications(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadedDate"));
        Page<CV> cvPage = cvRepository.findCVsUsedInApplications(pageable);

        List<CVResponse> cvResponses = cvPage.getContent().stream()
                .map(this::convertToCVResponse)
                .collect(Collectors.toList());

        return new CVPageResponse(
                cvResponses,
                cvPage.getNumber(),
                cvPage.getSize(),
                cvPage.getTotalPages(),
                cvPage.getTotalElements()
        );
    }

    @Override
    public CVPageResponse findRecentCVs(int page, int size) {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadedDate"));
        Page<CV> cvPage = cvRepository.findRecentCVs(thirtyDaysAgo, pageable);

        List<CVResponse> cvResponses = cvPage.getContent().stream()
                .map(this::convertToCVResponse)
                .collect(Collectors.toList());

        return new CVPageResponse(
                cvResponses,
                cvPage.getNumber(),
                cvPage.getSize(),
                cvPage.getTotalPages(),
                cvPage.getTotalElements()
        );
    }

    @Override
    public CVResponse findLatestCVByApplicant(String applicantId) {
        Pageable pageable = PageRequest.of(0, 1);
        List<CV> cvs = cvRepository.findLatestCVByApplicant(applicantId, pageable);

        if (cvs.isEmpty()) {
            throw new ResourceNotFoundException("No CV found for applicant: " + applicantId);
        }

        return convertToCVResponse(cvs.get(0));
    }

    @Override
    public List<CVResponse> findUnusedCVsByApplicant(String applicantId) {
        List<CV> cvs = cvRepository.findUnusedCVsByApplicant(applicantId);

        return cvs.stream()
                .map(this::convertToCVResponse)
                .collect(Collectors.toList());
    }

    @Override
    public long countCVsByApplicant(String applicantId) {
        return cvRepository.countByApplicantId(applicantId);
    }

    @Override
    public List<CVResponse> findCVsByUrlPattern(String urlPattern) {
        List<CV> cvs = cvRepository.findByUrlPattern(urlPattern);

        return cvs.stream()
                .map(this::convertToCVResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCVsByUrlPattern(String urlPattern) {
        List<CV> cvs = cvRepository.findByUrlPattern(urlPattern);
        cvRepository.deleteAll(cvs);
        log.info("CVs deleted by URL pattern: {}", urlPattern);
    }

    @Override
    public boolean isValidCVUrl(String url) {
        // Basic URL validation - can be enhanced based on requirements
        return url != null && !url.trim().isEmpty() &&
                (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("/"));
    }

    @Override
    public boolean isCVOwnedByApplicant(String cvId, String applicantId) {
        try {
            CV cv = getCVById(cvId);
            return cv.getApplicant().getId().equals(applicantId);
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    private CVResponse convertToCVResponse(CV cv) {
        boolean usedInAnyApplication = cv.getApplications() != null && !cv.getApplications().isEmpty();
        String latestApplicationId = usedInAnyApplication
                ? cv.getApplications().stream()
                .sorted((a, b) -> b.getAppliedAt().compareTo(a.getAppliedAt()))
                .map(Application::getId)
                .findFirst()
                .orElse(null)
                : null;

        return CVResponse.builder()
                .id(cv.getId())
                .url(cv.getUrl())
                .uploadedDate(cv.getUploadedDate())
                .applicantId(cv.getApplicant().getId())
                .applicantName(cv.getApplicant().getFirstName() + " " + cv.getApplicant().getLastName())
                .isUsedInApplication(usedInAnyApplication)
                .applicationId(latestApplicationId)
                .build();
    }
}
