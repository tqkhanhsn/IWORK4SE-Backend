package vn.iwork4se.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.iwork4se.controller.request.JobCategoryCreationRequest;
import vn.iwork4se.controller.request.JobCategoryUpdateRequest;
import vn.iwork4se.controller.response.JobCategoryCreationResponse;
import vn.iwork4se.controller.response.JobCategoryPageResponse;
import vn.iwork4se.controller.response.JobCategoryResponse;
import vn.iwork4se.exception.ResourceNotFoundException;
import vn.iwork4se.model.JobCategory;
import vn.iwork4se.repository.JobCategoryRepository;
import vn.iwork4se.service.JobCategoryService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobCategoryServiceImpl implements JobCategoryService {
    private final JobCategoryRepository jobCategoryRepository;

    // Tạo job category
    // truyền vào data ví dụ : categoryName=IT, description=Information Technology
    // categoryName là tên của category
    // description là mô tả của category
    @Override
    public JobCategoryCreationResponse save(JobCategoryCreationRequest request) {
        // Check if category name already exists
        if (jobCategoryRepository.existsByCategoryNameIgnoreCase(request.getCategoryName())) {
            throw new RuntimeException("Job category with name '" + request.getCategoryName() + "' already exists");
        }

        JobCategory jobCategory = JobCategory.builder()
                .categoryName(request.getCategoryName())
                .description(request.getDescription())
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        JobCategory savedCategory = jobCategoryRepository.save(jobCategory);

        log.info("Job category has been created successfully, categoryId={}", savedCategory.getId());

        return JobCategoryCreationResponse.builder()
                .id(savedCategory.getId())
                .categoryName(savedCategory.getCategoryName())
                .description(savedCategory.getDescription())
                .createAt(savedCategory.getCreateAt())
                .build();
    }

    // Cập nhật job category
    // truyền vào data ví dụ : id=1, categoryName=IT, description=Information Technology
    // id là id của category
    // categoryName là tên của category
    // description là mô tả của category
    @Override
    public void updateJobCategory(JobCategoryUpdateRequest request) {
        JobCategory jobCategory = getJobCategoryById(request.getId());

        // Check if new category name already exists (excluding current category)
        if (jobCategoryRepository.existsByCategoryNameIgnoreCaseAndIdNot(request.getCategoryName(), request.getId())) {
            throw new RuntimeException("Job category with name '" + request.getCategoryName() + "' already exists");
        }

        jobCategory.setCategoryName(request.getCategoryName());
        jobCategory.setDescription(request.getDescription());
        jobCategory.setUpdateAt(LocalDateTime.now());

        jobCategoryRepository.save(jobCategory);

        log.info("Job category has been updated successfully, categoryId={}", jobCategory.getId());
    }

    // Xóa job category
    // truyền vào data ví dụ : id=1
    // id là id của category
    @Override
    public void deleteJobCategory(Long id) {
        JobCategory jobCategory = getJobCategoryById(id);
        
        // Check if category has job posts
        long jobPostCount = countJobPostsByCategory(id);
        if (jobPostCount > 0) {
            throw new RuntimeException("Cannot delete category with " + jobPostCount + " job posts. Please remove job posts first.");
        }

        jobCategoryRepository.delete(jobCategory);
        log.info("Job category has been deleted successfully, categoryId={}", id);
    }

    // Lấy job category by id
    // truyền vào data ví dụ : id=1
    // id là id của category
    @Override
    public JobCategory getJobCategoryById(Long id) {
        return jobCategoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Job Category not found with id: " + id));
    }

    // Lấy tất cả job category
    // truyền vào data ví dụ : keyword=IT, sort=categoryName,asc, page=0, size=10
    // sort là tùy chọn, nếu không truyền thì mặc định là sort=categoryName,asc
    // page là trang hiện tại, size là số lượng item trên mỗi trang
    // keyword là từ khóa tìm kiếm, nếu không truyền thì mặc định là tìm tất cả
    @Override
    public JobCategoryPageResponse findAllJobCategories(String keyword, String sort, int page, int size) {
        // Create pageable with sorting
        Sort sortObj = Sort.by(Sort.Direction.ASC, "categoryName"); // Default sort by category name
        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            if (sortParams.length == 2) {
                Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? 
                    Sort.Direction.DESC : Sort.Direction.ASC;
                sortObj = Sort.by(direction, sortParams[0]);
            }
        }
        
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<JobCategory> categoryPage;
        
        // Search by keyword if provided
        if (keyword != null && !keyword.trim().isEmpty()) {
            categoryPage = jobCategoryRepository.findByKeyword(keyword.trim(), pageable);
        } else {
            categoryPage = jobCategoryRepository.findAll(pageable);
        }
        
        // Convert to response
        List<JobCategoryResponse> categoryResponses = categoryPage.getContent().stream()
                .map(this::convertToJobCategoryResponse)
                .collect(Collectors.toList());
        
        return new JobCategoryPageResponse(
                categoryResponses,
                categoryPage.getNumber(),
                categoryPage.getSize(),
                categoryPage.getTotalPages(),
                categoryPage.getTotalElements()
        );
    }

    // Lấy job category by id
    // truyền vào data ví dụ : id=1
    // id là id của category
    @Override
    public JobCategoryResponse findJobCategoryById(Long id) {
        JobCategory jobCategory = getJobCategoryById(id);
        return convertToJobCategoryResponse(jobCategory);
    }

    // Lấy job category by name
    // truyền vào data ví dụ : categoryName=IT, page=0, size=10
    // categoryName là tên của category
    // page là trang hiện tại, size là số lượng item trên mỗi trang
    @Override
    public JobCategoryPageResponse findJobCategoriesByName(String categoryName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "categoryName"));
        Page<JobCategory> categoryPage = jobCategoryRepository.findByCategoryNameContainingIgnoreCase(categoryName, pageable);
        
        List<JobCategoryResponse> categoryResponses = categoryPage.getContent().stream()
                .map(this::convertToJobCategoryResponse)
                .collect(Collectors.toList());
        
        return new JobCategoryPageResponse(
                categoryResponses,
                categoryPage.getNumber(),
                categoryPage.getSize(),
                categoryPage.getTotalPages(),
                categoryPage.getTotalElements()
        );
    }

    // Lấy job category by description
    // truyền vào data ví dụ : description=Technology, page=0, size=10
    // description là mô tả của category
    // page là trang hiện tại, size là số lượng item trên mỗi trang
    @Override
    public JobCategoryPageResponse findJobCategoriesByDescription(String description, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "categoryName"));
        Page<JobCategory> categoryPage = jobCategoryRepository.findByDescriptionContainingIgnoreCase(description, pageable);
        
        List<JobCategoryResponse> categoryResponses = categoryPage.getContent().stream()
                .map(this::convertToJobCategoryResponse)
                .collect(Collectors.toList());
        
        return new JobCategoryPageResponse(
                categoryResponses,
                categoryPage.getNumber(),
                categoryPage.getSize(),
                categoryPage.getTotalPages(),
                categoryPage.getTotalElements()
        );
    }

    // Lấy job category có nhiều job posts nhất
    // truyền vào data ví dụ : page=0, size=10
    // page là trang hiện tại, size là số lượng item trên mỗi trang
    @Override
    public JobCategoryPageResponse findJobCategoriesWithMostJobPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<JobCategory> categories = jobCategoryRepository.findCategoriesWithMostJobPosts(pageable);
        
        List<JobCategoryResponse> categoryResponses = categories.stream()
                .map(this::convertToJobCategoryResponse)
                .collect(Collectors.toList());
        
        // For simplicity, we'll return a page response with the list
        // In a real scenario, you might want to implement proper pagination
        return new JobCategoryPageResponse(
                categoryResponses,
                page,
                size,
                (int) Math.ceil((double) categories.size() / size),
                categories.size()
        );
    }

    // Lấy job category by name (exact match)
    // truyền vào data ví dụ : categoryName=IT
    // categoryName là tên của category
    @Override
    public JobCategoryResponse findJobCategoryByName(String categoryName) {
        JobCategory jobCategory = jobCategoryRepository.findByCategoryNameIgnoreCase(categoryName)
                .orElseThrow(() -> new ResourceNotFoundException("Job Category not found with name: " + categoryName));
        return convertToJobCategoryResponse(jobCategory);
    }

    // Đếm job posts by category id
    // truyền vào data ví dụ : categoryId=1
    // categoryId là id của category
    // trả về số lượng job posts của category
    @Override
    public long countJobPostsByCategory(Long categoryId) {
        return jobCategoryRepository.countJobPostsByCategoryId(categoryId);
    }

    // Kiểm tra category name có tồn tại không
    // truyền vào data ví dụ : categoryName=IT
    // categoryName là tên của category
    // trả về true nếu tồn tại, false nếu không
    @Override
    public boolean existsByCategoryName(String categoryName) {
        return jobCategoryRepository.existsByCategoryNameIgnoreCase(categoryName);
    }

    // Kiểm tra category name có tồn tại không (loại trừ category hiện tại)
    // truyền vào data ví dụ : categoryName=IT, excludeId=1
    // categoryName là tên của category
    // excludeId là id của category cần loại trừ
    // trả về true nếu tồn tại, false nếu không
    @Override
    public boolean existsByCategoryNameExcludingId(String categoryName, Long excludeId) {
        return jobCategoryRepository.existsByCategoryNameIgnoreCaseAndIdNot(categoryName, excludeId);
    }

    // Lấy tất cả job categories (không phân trang)
    // trả về danh sách tất cả categories
    @Override
    public List<JobCategoryResponse> getAllJobCategories() {
        List<JobCategory> categories = jobCategoryRepository.findAll(Sort.by(Sort.Direction.ASC, "categoryName"));
        return categories.stream()
                .map(this::convertToJobCategoryResponse)
                .collect(Collectors.toList());
    }

    // Helper method to convert JobCategory entity to JobCategoryResponse
    // truyền vào data ví dụ : jobCategory=JobCategory(id=1, categoryName=IT, description=Technology, createAt=2025-01-01, updateAt=2025-01-01)
    // jobCategory là job category cần chuyển đổi
    // trả về job category response
    // chức năng để chuyển đổi job category entity thành job category response
    // dùng để lấy data cho job category response cho frontend
    private JobCategoryResponse convertToJobCategoryResponse(JobCategory jobCategory) {
        long jobPostCount = countJobPostsByCategory(jobCategory.getId());
        
        return JobCategoryResponse.builder()
                .id(jobCategory.getId())
                .categoryName(jobCategory.getCategoryName())
                .description(jobCategory.getDescription())
                .createAt(jobCategory.getCreateAt())
                .updateAt(jobCategory.getUpdateAt())
                .jobPostCount(jobPostCount)
                .build();
    }
}
