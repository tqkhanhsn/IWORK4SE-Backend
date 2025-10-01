package vn.iwork4se.service;

import vn.iwork4se.controller.request.JobPostCreationRequest;
import vn.iwork4se.controller.request.JobPostUpdateRequest;
import vn.iwork4se.controller.response.JobPostCreationResponse;
import vn.iwork4se.controller.response.JobPostPageResponse;
import vn.iwork4se.controller.response.JobPostResponse;
import vn.iwork4se.model.JobPost;

public interface JobPostService {
    // CRUD methods for JobPost entity
    JobPostCreationResponse save(JobPostCreationRequest request) ;
    void updateJobPost(JobPostUpdateRequest request);
    void deleteJobPost(String id);
    JobPost getJobPostById(String id);
    JobPostPageResponse findAllJobPosts(String keyword, String sort, int page, int size);
    JobPostResponse findJobPostById(String id);
    
    // Additional business methods
    JobPostPageResponse findJobPostsByEmployer(String employerId, int page, int size);
    JobPostPageResponse findActiveJobPosts(String keyword, String sort, int page, int size);
    JobPostPageResponse findJobPostsByStatus(String status, int page, int size);
    JobPostPageResponse findJobPostsByType(String jobType, int page, int size);
    JobPostPageResponse findJobPostsByLocation(String location, int page, int size);
    JobPostPageResponse findJobPostsBySalaryRange(Double minSalary, Double maxSalary, int page, int size);
    JobPostPageResponse findJobPostsByCategory(Long categoryId, int page, int size);
    JobPostPageResponse searchJobPostsWithMultipleCriteria(String keyword, String status, String jobType, 
                                                          String location, Double minSalary, Double maxSalary, 
                                                          String sort, int page, int size);
    long countJobPostsByEmployer(String employerId);
    void updateJobPostStatus(String id, String status);

}
