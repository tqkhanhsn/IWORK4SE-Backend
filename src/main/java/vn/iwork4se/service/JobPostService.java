package vn.iwork4se.service;

import vn.iwork4se.controller.request.JobPostCreationRequest;
import vn.iwork4se.controller.response.JobPostCreationResponse;
import vn.iwork4se.model.JobPost;

public interface JobPostService {
    // CRUD methods for JobPost entity
    JobPostCreationResponse save(JobPostCreationRequest request) ;
    void updateJobPost(JobPost jobPost);
    void deleteJobPost(String id);
    JobPostCreationResponse getJobPostById(String id);

}
