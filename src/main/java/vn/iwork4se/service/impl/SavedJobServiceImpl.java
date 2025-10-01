package vn.iwork4se.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.iwork4se.controller.request.SavedJobCreationRequest;
import vn.iwork4se.controller.response.SavedJobCreationResponse;
import vn.iwork4se.controller.response.SavedJobPageResponse;
import vn.iwork4se.controller.response.SavedJobResponse;
import vn.iwork4se.exception.ResourceNotFoundException;
import vn.iwork4se.model.Applicant;
import vn.iwork4se.model.JobPost;
import vn.iwork4se.model.SavedJob;
import vn.iwork4se.repository.ApplicantRepository;
import vn.iwork4se.repository.JobPostRepository;
import vn.iwork4se.repository.SavedJobRepository;
import vn.iwork4se.service.SavedJobService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SavedJobServiceImpl implements SavedJobService {
    private final SavedJobRepository savedJobRepository;
    private final ApplicantRepository applicantRepository;
    private final JobPostRepository jobPostRepository;

    @Override
    public SavedJobCreationResponse save(SavedJobCreationRequest request) {
        // Check if job is already saved by applicant
        Optional<SavedJob> existingSavedJob = savedJobRepository.findByApplicantIdAndJobId(
                request.getApplicantId(), request.getJobId());
        
        if (existingSavedJob.isPresent()) {
            throw new RuntimeException("Job is already saved by this applicant");
        }

        Applicant applicant = applicantRepository.findById(request.getApplicantId())
                .orElseThrow(() -> new RuntimeException("Applicant not found"));
        
        JobPost jobPost = jobPostRepository.findById(request.getJobId())
                .orElseThrow(() -> new RuntimeException("Job post not found"));

        SavedJob savedJob = SavedJob.builder()
                .id("SJ" + UUID.randomUUID().toString())
                .applicant(applicant)
                .job(jobPost)
                .savedDate(LocalDate.now())
                .build();

        SavedJob savedJobSaved = savedJobRepository.save(savedJob);
        log.info("Job saved successfully, savedJobId={}", savedJobSaved.getId());

        return SavedJobCreationResponse.builder()
                .id(savedJobSaved.getId())
                .applicantId(savedJobSaved.getApplicant().getId())
                .jobId(savedJobSaved.getJob().getId())
                .savedDate(savedJobSaved.getSavedDate())
                .build();
    }

    @Override
    public void deleteSavedJob(String id) {
        SavedJob savedJob = getSavedJobById(id);
        savedJobRepository.delete(savedJob);
        log.info("Saved job deleted successfully, savedJobId={}", id);
    }

    @Override
    public void deleteSavedJobByApplicantAndJob(String applicantId, String jobId) {
        Optional<SavedJob> savedJob = savedJobRepository.findByApplicantIdAndJobId(applicantId, jobId);
        if (savedJob.isPresent()) {
            savedJobRepository.delete(savedJob.get());
            log.info("Saved job deleted successfully, applicantId={}, jobId={}", applicantId, jobId);
        } else {
            throw new ResourceNotFoundException("Saved job not found for applicant and job");
        }
    }

    @Override
    public SavedJob getSavedJobById(String id) {
        return savedJobRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Saved job not found with id: " + id));
    }

    @Override
    public SavedJobResponse findSavedJobById(String id) {
        SavedJob savedJob = getSavedJobById(id);
        return convertToSavedJobResponse(savedJob);
    }

    @Override
    public SavedJobPageResponse findSavedJobsByApplicant(String applicantId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "savedDate"));
        Page<SavedJob> savedJobPage = savedJobRepository.findByApplicantId(applicantId, pageable);
        
        List<SavedJobResponse> savedJobResponses = savedJobPage.getContent().stream()
                .map(this::convertToSavedJobResponse)
                .collect(Collectors.toList());
        
        return new SavedJobPageResponse(
                savedJobResponses,
                savedJobPage.getNumber(),
                savedJobPage.getSize(),
                savedJobPage.getTotalPages(),
                savedJobPage.getTotalElements()
        );
    }

    @Override
    public SavedJobPageResponse findSavedJobsByJob(String jobId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "savedDate"));
        Page<SavedJob> savedJobPage = savedJobRepository.findByJobId(jobId, pageable);
        
        List<SavedJobResponse> savedJobResponses = savedJobPage.getContent().stream()
                .map(this::convertToSavedJobResponse)
                .collect(Collectors.toList());
        
        return new SavedJobPageResponse(
                savedJobResponses,
                savedJobPage.getNumber(),
                savedJobPage.getSize(),
                savedJobPage.getTotalPages(),
                savedJobPage.getTotalElements()
        );
    }

    @Override
    public SavedJobPageResponse findSavedJobsByApplicantAndDateRange(String applicantId, LocalDate startDate, LocalDate endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "savedDate"));
        Page<SavedJob> savedJobPage = savedJobRepository.findByApplicantIdAndDateRange(applicantId, startDate, endDate, pageable);
        
        List<SavedJobResponse> savedJobResponses = savedJobPage.getContent().stream()
                .map(this::convertToSavedJobResponse)
                .collect(Collectors.toList());
        
        return new SavedJobPageResponse(
                savedJobResponses,
                savedJobPage.getNumber(),
                savedJobPage.getSize(),
                savedJobPage.getTotalPages(),
                savedJobPage.getTotalElements()
        );
    }

    @Override
    public SavedJobPageResponse findRecentSavedJobsByApplicant(String applicantId, int page, int size) {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "savedDate"));
        Page<SavedJob> savedJobPage = savedJobRepository.findRecentSavedJobsByApplicant(applicantId, thirtyDaysAgo, pageable);
        
        List<SavedJobResponse> savedJobResponses = savedJobPage.getContent().stream()
                .map(this::convertToSavedJobResponse)
                .collect(Collectors.toList());
        
        return new SavedJobPageResponse(
                savedJobResponses,
                savedJobPage.getNumber(),
                savedJobPage.getSize(),
                savedJobPage.getTotalPages(),
                savedJobPage.getTotalElements()
        );
    }

    @Override
    public boolean isJobSavedByApplicant(String applicantId, String jobId) {
        return savedJobRepository.findByApplicantIdAndJobId(applicantId, jobId).isPresent();
    }

    @Override
    public long countSavedJobsByApplicant(String applicantId) {
        return savedJobRepository.countByApplicantId(applicantId);
    }

    @Override
    public long countSavedJobsByJob(String jobId) {
        return savedJobRepository.countByJobId(jobId);
    }

    @Override
    public SavedJobCreationResponse toggleSaveJob(String applicantId, String jobId) {
        Optional<SavedJob> existingSavedJob = savedJobRepository.findByApplicantIdAndJobId(applicantId, jobId);
        
        if (existingSavedJob.isPresent()) {
            // Unsave the job
            savedJobRepository.delete(existingSavedJob.get());
            log.info("Job unsaved successfully, applicantId={}, jobId={}", applicantId, jobId);
            return null; // Indicate job was unsaved
        } else {
            // Save the job
            SavedJobCreationRequest request = SavedJobCreationRequest.builder()
                    .applicantId(applicantId)
                    .jobId(jobId)
                    .build();
            return save(request);
        }
    }

    private SavedJobResponse convertToSavedJobResponse(SavedJob savedJob) {
        return SavedJobResponse.builder()
                .id(savedJob.getId())
                .applicantId(savedJob.getApplicant().getId())
                .applicantName(savedJob.getApplicant().getFirstName() + " " + savedJob.getApplicant().getLastName())
                .jobId(savedJob.getJob().getId())
                .jobTitle(savedJob.getJob().getTitle())
                .jobPosition(savedJob.getJob().getJobPosition())
                .jobLocation(savedJob.getJob().getLocation())
                .companyName(savedJob.getJob().getEmployer() != null ? savedJob.getJob().getEmployer().getCompanyName() : null)
                .savedDate(savedJob.getSavedDate())
                .build();
    }
}
