package vn.iwork4se.service;

import vn.iwork4se.controller.request.JobCategoryCreationRequest;
import vn.iwork4se.controller.request.JobCategoryUpdateRequest;
import vn.iwork4se.controller.response.JobCategoryCreationResponse;
import vn.iwork4se.controller.response.JobCategoryPageResponse;
import vn.iwork4se.controller.response.JobCategoryResponse;
import vn.iwork4se.model.JobCategory;

import java.util.List;

public interface JobCategoryService {
    // CRUD methods for JobCategory entity
    JobCategoryCreationResponse save(JobCategoryCreationRequest request);
    void updateJobCategory(JobCategoryUpdateRequest request);
    void deleteJobCategory(Long id);
    JobCategory getJobCategoryById(Long id);
    JobCategoryPageResponse findAllJobCategories(String keyword, String sort, int page, int size);
    JobCategoryResponse findJobCategoryById(Long id);
    
    // Additional business methods
    JobCategoryPageResponse findJobCategoriesByName(String categoryName, int page, int size);
    JobCategoryPageResponse findJobCategoriesByDescription(String description, int page, int size);
    JobCategoryPageResponse findJobCategoriesWithMostJobPosts(int page, int size);
    JobCategoryResponse findJobCategoryByName(String categoryName);
    long countJobPostsByCategory(Long categoryId);
    boolean existsByCategoryName(String categoryName);
    boolean existsByCategoryNameExcludingId(String categoryName, Long excludeId);
    List<JobCategoryResponse> getAllJobCategories();
}
