package vn.iwork4se.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.iwork4se.common.JobStatus;
import vn.iwork4se.common.JobType;
import vn.iwork4se.controller.request.JobPostCreationRequest;
import vn.iwork4se.controller.request.JobPostUpdateRequest;
import vn.iwork4se.controller.response.JobPostCreationResponse;
import vn.iwork4se.controller.response.JobPostPageResponse;
import vn.iwork4se.controller.response.JobPostResponse;
import vn.iwork4se.exception.ResourceNotFoundException;
import vn.iwork4se.model.Employer;
import vn.iwork4se.model.JobCategory;
import vn.iwork4se.model.JobPost;
import vn.iwork4se.repository.EmployerRepository;
import vn.iwork4se.repository.JobCategoryRepository;
import vn.iwork4se.repository.JobPostRepository;
import vn.iwork4se.service.JobPostService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobPostServiceImpl implements JobPostService {
    private final JobPostRepository jobPostRepository;
    private final EmployerRepository employerRepository;
    private final JobCategoryRepository jobCategoryRepository;
    // CRUD methods for JobPost entity

    // Tạo job post
    // truyền vào data ví dụ : title=java, description=java, jobPosition=java, location=java, experience=java, minSalary=1000, maxSalary=2000, vacancies=10, jobType=FULL_TIME, employerId=1, categoryId=1
    // title là tiêu đề của job post
    // description là mô tả của job post
    // jobPosition là vị trí của job post
    // location là địa chỉ của job post
    // experience là kinh nghiệm của job post
    // minSalary là mức lương tối thiểu của job post
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
        jobPost.setJobStatus(JobStatus.PENDING);  // Ơending là trạng thái đợi duyệt, để apply cần chuyn sang
        jobPost.setJobType(JobType.valueOf(request.getJobType().toUpperCase()));
        jobPost.setUpdateAt(LocalDateTime.now());
        Employer employer = employerRepository.findById(request.getEmployerId())
                .orElseThrow(() -> new RuntimeException("Employer not found"));
        jobPost.setEmployer(employer);

        // Set category if provided 
        if (request.getCategoryId() != null && !request.getCategoryId().trim().isEmpty()) {
            try {
                Long categoryId = Long.parseLong(request.getCategoryId());
                JobCategory category = jobCategoryRepository.findById(categoryId)
                        .orElseThrow(() -> new RuntimeException("Job category not found"));
                jobPost.setCategory(category);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid category ID format: " + request.getCategoryId());
            }
        }

        JobPost jpSave = jobPostRepository.save(jobPost);

        log.info("Job post has added successfully, userId={}", jobPost.getId());

        return JobPostCreationResponse.builder()
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


    // Cập nhật job post
    // truyền vào data ví dụ : id=1, title=java, description=java, jobPosition=java, location=java, experience=java, minSalary=1000, maxSalary=2000, vacancies=10, jobType=FULL_TIME, employerId=1, categoryId=1
    // id là id của job post
    // title là tiêu đề của job post
    // description là mô tả của job post
    // jobPosition là vị trí của job post
    // location là địa chỉ của job post
    // experience là kinh nghiệm của job post
    // minSalary là mức lương tối thiểu của job post
    // maxSalary là mức lương tối đa của job post
    // vacancies là số lượng việc làm cần tuyển
    // jobType là loại việc làm
    // employerId là id của employer
    // categoryId là id của category
    @Override
    public void updateJobPost(JobPostUpdateRequest request) {
        JobPost jobPost = getJobPostById(request.getId());

        jobPost.setTitle(request.getTitle());
        jobPost.setDescription(request.getDescription());
        jobPost.setJobPosition(request.getJobPosition());
        jobPost.setLocation(request.getLocation());
        jobPost.setExperience(request.getExperience());
        jobPost.setMinSalary(request.getMinSalary());
        jobPost.setMaxSalary(request.getMaxSalary());
        jobPost.setVacancies(request.getVacancies());
        jobPost.setJobType(JobType.valueOf(request.getJobType().toUpperCase()));
        jobPost.setUpdateAt(LocalDateTime.now());
        Employer employer = employerRepository.findById(request.getEmployerId())
                .orElseThrow(() -> new RuntimeException("Employer not found"));
        jobPost.setEmployer(employer);

        // Set category if provided
        if (request.getCategoryId() != null && !request.getCategoryId().trim().isEmpty()) {
            try {
                Long categoryId = Long.parseLong(request.getCategoryId());
                JobCategory category = jobCategoryRepository.findById(categoryId)
                        .orElseThrow(() -> new RuntimeException("Job category not found"));
                jobPost.setCategory(category);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid category ID format: " + request.getCategoryId());
            }
        } else {
            jobPost.setCategory(null); // Remove category if not provided
        }

        jobPostRepository.save(jobPost);

        log.info("Job post has updated successfully, userId={}", jobPost.getId());
    }

    // Xóa job post
    // truyền vào data ví dụ : id=1
    // id là id của job post
    @Override
    public void deleteJobPost(String id) {
        JobPost jobPost = getJobPostById(id);
        jobPostRepository.delete(jobPost);
        log.info("Job post has been deleted successfully, jobPostId={}", id);
    }

    // Lấy job post by id
    // truyền vào data ví dụ : id=1
    // id là id của job post
    @Override
    public JobPost getJobPostById(String id) {
        return jobPostRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Job Post not found with id: "+ id));
    }

    // Lấy tất cả job post
    // truyền vào data ví dụ : keyword=java, sort=postedDate,desc, page=0, size=10.
    // sort là tùy chọn, nếu không truyền thì mặc định là sort=postedDate,desc
    // page là trang hiện tại, size là số lượng item trên mỗi trang
    // keyword là từ khóa tìm kiếm, nếu không truyền thì mặc định là tìm tất cả
    @Override
    public JobPostPageResponse findAllJobPosts(String keyword, String sort, int page, int size) {
        // Create pageable with sorting
        Sort sortObj = Sort.by(Sort.Direction.DESC, "postedDate"); // Default sort by posted date
        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            if (sortParams.length == 2) {
                Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ?
                        Sort.Direction.DESC : Sort.Direction.ASC;
                sortObj = Sort.by(direction, sortParams[0]);
            }
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<JobPost> jobPostPage;

        // Search by keyword if provided
        if (keyword != null && !keyword.trim().isEmpty()) {
            jobPostPage = jobPostRepository.findByKeyword(keyword.trim(), pageable);
        } else {
            jobPostPage = jobPostRepository.findAll(pageable);
        }

        // Convert to response
        List<JobPostResponse> jobPostResponses = jobPostPage.getContent().stream()
                .map(this::convertToJobPostResponse)
                .collect(Collectors.toList());

        return new JobPostPageResponse(
                jobPostResponses,
                jobPostPage.getNumber(),
                jobPostPage.getSize(),
                jobPostPage.getTotalPages(),
                jobPostPage.getTotalElements()
        );
    }

    // Lấy job post by id
    // truyền vào data ví dụ : id=1
    // id là id của job post
    @Override
    public JobPostResponse findJobPostById(String id) {
        JobPost jobPost = getJobPostById(id);
        return convertToJobPostResponse(jobPost);
    }

    // Lấy job post by employer id
    // truyền vào data ví dụ : employerId=1, page=0, size=10.
    // employerId là id của employer
    // page là trang hiện tại, size là số lượng item trên mỗi trang
    @Override
    public JobPostPageResponse findJobPostsByEmployer(String employerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "postedDate"));
        Page<JobPost> jobPostPage = jobPostRepository.findByEmployerId(employerId, pageable);

        List<JobPostResponse> jobPostResponses = jobPostPage.getContent().stream()
                .map(this::convertToJobPostResponse)
                .collect(Collectors.toList());

        return new JobPostPageResponse(
                jobPostResponses,
                jobPostPage.getNumber(),
                jobPostPage.getSize(),
                jobPostPage.getTotalPages(),
                jobPostPage.getTotalElements()
        );
    }

    // Lấy job post by active . định nghĩa như thế nào về active? 
    // active là job post có trạng thái là ACCEPTED và ngày đóng cửa lớn hơn ngày hiện tại
    // truyền vào data ví dụ : keyword=java, sort=postedDate,desc, page=0, size=10.
    // sort là tùy chọn, nếu không truyền thì mặc định là sort=postedDate,desc
    // page là trang hiện tại, size là số lượng item trên mỗi trang
    // keyword là từ khóa tìm kiếm, nếu không truyền thì mặc định là tìm tất cả
    // status là trạng thái của job post, nếu không truyền thì mặc định là status=ACCEPTED
    @Override
    public JobPostPageResponse findActiveJobPosts(String keyword, String sort, int page, int size) {
        Sort sortObj = Sort.by(Sort.Direction.DESC, "postedDate");
        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            if (sortParams.length == 2) {
                Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ?
                        Sort.Direction.DESC : Sort.Direction.ASC;
                sortObj = Sort.by(direction, sortParams[0]);
            }
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<JobPost> jobPostPage = jobPostRepository.findActiveJobPosts(JobStatus.ACCEPTED, LocalDate.now(), pageable);

        List<JobPostResponse> jobPostResponses = jobPostPage.getContent().stream()
                .map(this::convertToJobPostResponse)
                .collect(Collectors.toList());

        return new JobPostPageResponse(
                jobPostResponses,
                jobPostPage.getNumber(),
                jobPostPage.getSize(),
                jobPostPage.getTotalPages(),
                jobPostPage.getTotalElements()
        );
    }

    // Lấy job post by status
    // truyền vào data ví dụ : status=ACCEPTED, page=0, size=10.
    // status là trạng thái của job post
    // page là trang hiện tại, size là số lượng item trên mỗi trang
    @Override
    public JobPostPageResponse findJobPostsByStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "postedDate"));
        JobStatus jobStatus = JobStatus.valueOf(status.toUpperCase());
        Page<JobPost> jobPostPage = jobPostRepository.findByJobStatus(jobStatus, pageable);

        List<JobPostResponse> jobPostResponses = jobPostPage.getContent().stream()
                .map(this::convertToJobPostResponse)
                .collect(Collectors.toList());

        return new JobPostPageResponse(
                jobPostResponses,
                jobPostPage.getNumber(),
                jobPostPage.getSize(),
                jobPostPage.getTotalPages(),
                jobPostPage.getTotalElements()
        );
    }

    // Lấy job post by type
    // truyền vào data ví dụ : jobType=FULL_TIME, page=0, size=10.
    // jobType là loại việc làm
    // page là trang hiện tại, size là số lượng item trên mỗi trang
    @Override
    public JobPostPageResponse findJobPostsByType(String jobType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "postedDate"));
        JobType type = JobType.valueOf(jobType.toUpperCase());
        Page<JobPost> jobPostPage = jobPostRepository.findByJobType(type, pageable);

        List<JobPostResponse> jobPostResponses = jobPostPage.getContent().stream()
                .map(this::convertToJobPostResponse)
                .collect(Collectors.toList());

        return new JobPostPageResponse(
                jobPostResponses,
                jobPostPage.getNumber(),
                jobPostPage.getSize(),
                jobPostPage.getTotalPages(),
                jobPostPage.getTotalElements()
        );
    }

    // Lấy job post by location
    // truyền vào data ví dụ : location=Ha Noi, page=0, size=10.
    // location là địa chỉ của job post
    // page là trang hiện tại, size là số lượng item trên mỗi trang
    @Override
    public JobPostPageResponse findJobPostsByLocation(String location, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "postedDate"));
        Page<JobPost> jobPostPage = jobPostRepository.findByLocationContainingIgnoreCase(location, pageable);

        List<JobPostResponse> jobPostResponses = jobPostPage.getContent().stream()
                .map(this::convertToJobPostResponse)
                .collect(Collectors.toList());

        return new JobPostPageResponse(
                jobPostResponses,
                jobPostPage.getNumber(),
                jobPostPage.getSize(),
                jobPostPage.getTotalPages(),
                jobPostPage.getTotalElements()
        );
    }

    // Lấy job post by salary range
    // truyền vào data ví dụ : minSalary=1000, maxSalary=2000, page=0, size=10.
    // minSalary là mức lương tối thiểu củ
    // maxSalary là mức lương tối đa của job post
    // page là trang hiện tại, size là số lượng item trên mỗi trang
    @Override
    public JobPostPageResponse findJobPostsBySalaryRange(Double minSalary, Double maxSalary, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "postedDate"));
        Page<JobPost> jobPostPage = jobPostRepository.findBySalaryRange(minSalary, maxSalary, pageable);

        List<JobPostResponse> jobPostResponses = jobPostPage.getContent().stream()
                .map(this::convertToJobPostResponse)
                .collect(Collectors.toList());

        return new JobPostPageResponse(
                jobPostResponses,
                jobPostPage.getNumber(),
                jobPostPage.getSize(),
                jobPostPage.getTotalPages(),
                jobPostPage.getTotalElements()
        );
    }

    // Lấy job post by category
    // truyền vào data ví dụ : categoryId=1, page=0, size=10.
    // categoryId là id của category
    // page là trang hiện tại, size là số lượng item trên mỗi trang
    @Override
    public JobPostPageResponse findJobPostsByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "postedDate"));
        Page<JobPost> jobPostPage = jobPostRepository.findByCategoryId(categoryId, pageable);

        List<JobPostResponse> jobPostResponses = jobPostPage.getContent().stream()
                .map(this::convertToJobPostResponse)
                .collect(Collectors.toList());

        return new JobPostPageResponse(
                jobPostResponses,
                jobPostPage.getNumber(),
                jobPostPage.getSize(),
                jobPostPage.getTotalPages(),
                jobPostPage.getTotalElements()
        );
    }

    // Tìm kiếm job post với nhiều tiêu chí
    // truyền vào data ví dụ : keyword=java, status=ACCEPTED, jobType=FULL_TIME, location=Ha Noi, minSalary=1000, maxSalary=2000, sort=postedDate,desc, page=0, size=10
    // keyword là từ khóa tìm kiếm (tùy chọn)
    // status là trạng thái job post (tùy chọn)
    // jobType là loại việc làm (tùy chọn)
    // location là địa điểm (tùy chọn)
    // minSalary là mức lương tối thiểu (tùy chọn)
    // maxSalary là mức lương tối đa (tùy chọn)
    // sort là sắp xếp (tùy chọn)
    // page là trang hiện tại, size là số lượng item trên mỗi trang
    @Override
    public JobPostPageResponse searchJobPostsWithMultipleCriteria(String keyword, String status, String jobType,
                                                                  String location, Double minSalary, Double maxSalary,
                                                                  String sort, int page, int size) {
        // Create pageable with sorting
        Sort sortObj = Sort.by(Sort.Direction.DESC, "postedDate"); // Default sort by posted date
        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            if (sortParams.length == 2) {
                Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ?
                        Sort.Direction.DESC : Sort.Direction.ASC;
                sortObj = Sort.by(direction, sortParams[0]);
            }
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);

        // Convert string parameters to enums if provided
        JobStatus jobStatus = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                jobStatus = JobStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid job status: " + status);
            }
        }

        JobType type = null;
        if (jobType != null && !jobType.trim().isEmpty()) {
            try {
                type = JobType.valueOf(jobType.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid job type: " + jobType);
            }
        }

        // Use the repository method for multiple criteria search
        Page<JobPost> jobPostPage = jobPostRepository.findByMultipleCriteria(
                keyword, jobStatus, type, location, minSalary, maxSalary, pageable);

        // Convert to response
        List<JobPostResponse> jobPostResponses = jobPostPage.getContent().stream()
                .map(this::convertToJobPostResponse)
                .collect(Collectors.toList());

        return new JobPostPageResponse(
                jobPostResponses,
                jobPostPage.getNumber(),
                jobPostPage.getSize(),
                jobPostPage.getTotalPages(),
                jobPostPage.getTotalElements()
        );
    }

    // Đếm job post by employer id
    // truyền vào data ví dụ : employerId=1
    // employerId là id của employer
    // trả về số lượng job post của employer
    @Override
    public long countJobPostsByEmployer(String employerId) {
        return jobPostRepository.countByEmployerId(employerId);
    }

    // Cập nhật trạng thái của job post
    // truyền vào data ví dụ : id=1, status=ACCEPTED
    // id là id của job post
    // status là trạng thái của job post
    @Override
    public void updateJobPostStatus(String id, String status) {
        JobPost jobPost = getJobPostById(id);
        JobStatus jobStatus = JobStatus.valueOf(status.toUpperCase());
        jobPost.setJobStatus(jobStatus);
        jobPost.setUpdateAt(LocalDateTime.now());
        jobPostRepository.save(jobPost);
        log.info("Job post status updated successfully, jobPostId={}, newStatus={}", id, status);
    }

    // Helper method to convert JobPost entity to JobPostResponse
    // truyền vào data ví dụ : jobPost=JobPost(id=1, title=java, description=java, jobPosition=java, location=java, experience=java, minSalary=1000, maxSalary=2000, vacancies=10, jobStatus=ACCEPTED, jobType=FULL_TIME, updateAt=2025-01-01, employer=Employer(id=1, companyName=java), category=JobCategory(id=1, categoryName=java))
    // jobPost là job post cần chuyển đổi
    // trả về job post response
    // chức năng để chuyển đổi job post entity thành job post response
    // dùng để lấy data cho job post response cho frontend
    // dùng để lấy data cho job post response cho backend
    // dùng để lấy data cho job post response cho backend
    private JobPostResponse convertToJobPostResponse(JobPost jobPost) {
        return JobPostResponse.builder()
                .id(jobPost.getId())
                .title(jobPost.getTitle())
                .description(jobPost.getDescription())
                .jobPosition(jobPost.getJobPosition())
                .location(jobPost.getLocation())
                .experience(jobPost.getExperience())
                .minSalary(jobPost.getMinSalary())
                .maxSalary(jobPost.getMaxSalary())
                .postedDate(jobPost.getPostedDate())
                .closingDate(jobPost.getClosingDate())
                .vacancies(jobPost.getVacancies())
                .jobStatus(jobPost.getJobStatus())
                .jobType(jobPost.getJobType())
                .updateAt(jobPost.getUpdateAt())
                .employerId(jobPost.getEmployer() != null ? jobPost.getEmployer().getId() : null)
                .employerName(jobPost.getEmployer() != null ? jobPost.getEmployer().getCompanyName() : null)
                .companyName(jobPost.getEmployer() != null ? jobPost.getEmployer().getCompanyName() : null)
                .logoUrl(jobPost.getEmployer() != null ? jobPost.getEmployer().getLogoUrl() : null)
                .categoryId(jobPost.getCategory() != null ? jobPost.getCategory().getId().toString() : null)
                .categoryName(jobPost.getCategory() != null ? jobPost.getCategory().getCategoryName() : null)
                .build();
    }

}
