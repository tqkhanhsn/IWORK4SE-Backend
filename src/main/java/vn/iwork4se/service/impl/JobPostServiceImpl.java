package vn.iwork4se.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.iwork4se.common.JobStatus;

import vn.iwork4se.common.JobType;
import vn.iwork4se.controller.request.JobPostCreationRequest;
import vn.iwork4se.controller.response.JobPostCreationResponse;
import vn.iwork4se.model.Employer;
import vn.iwork4se.model.JobPost;
import vn.iwork4se.repository.EmployerRepository;
import vn.iwork4se.repository.JobPostRepository;
import vn.iwork4se.service.JobPostService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobPostServiceImpl implements JobPostService {
    private final JobPostRepository jobPostRepository;
    private final EmployerRepository employerRepository;
    // CRUD methods for JobPost entity
    @Override
    public JobPostCreationResponse save(JobPostCreationRequest request) {

        JobPost jobPost = new JobPost();
        jobPost.setId("JP"+ UUID.randomUUID().toString());
        jobPost.setTitle(request.getTitle());
        jobPost.setDescription(request.getDescription());
        jobPost.setJobPosition(request.getJobPosition());
        jobPost.setLocation(request.getLocation());
        jobPost.setExperience(request.getExperience());
        jobPost.setMinSalary(request.getMinSalary());
        jobPost.setMaxSalary(request.getMaxSalary());
        jobPost.setPostedDate(LocalDate.now());
        // 30 ngày kể từ ngày tạo
        jobPost.setClosingDate(jobPost.getPostedDate().plusDays(30));
        jobPost.setVacancies(request.getVacancies());
        jobPost.setJobStatus(JobStatus.PENDING);
        jobPost.setJobType(JobType.valueOf(request.getJobType().toUpperCase()));
        jobPost.setUpdateAt(LocalDateTime.now());
        Employer employer = employerRepository.findById(request.getEmployerId())
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        jobPost.setEmployer(employer);
        JobPost jpSave = jobPostRepository.save(jobPost);

        log.info("Job post has added successfully, userId={}", jobPost.getId());

        return JobPostCreationResponse.builder() // có cần id không
                .id(jpSave.getId())
                .title(jpSave.getTitle())
                .description(jpSave.getDescription())
                .location(jpSave.getLocation())
                .experience(jpSave.getExperience())
                .jobPosition(jpSave.getJobPosition())
                .jobType(jpSave.getJobType())
                .closingDate(jobPost.getClosingDate())
                .minSalary(jobPost.getMinSalary())
                .employerId(jpSave.getEmployer().getId())
                .build();
    }

    @Override
    public void updateJobPost(JobPost jobPost) {

    }

    @Override
    public void deleteJobPost(String id) {

    }

    @Override
    public JobPostCreationResponse getJobPostById(String id) {
        return null;
    }

}
