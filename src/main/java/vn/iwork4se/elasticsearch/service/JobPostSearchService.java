package vn.iwork4se.elasticsearch.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.iwork4se.common.JobStatus;
import vn.iwork4se.common.JobType;
import vn.iwork4se.elasticsearch.document.JobPostDocument;

import java.time.LocalDate;

public interface JobPostSearchService {

    // Index a job post
    JobPostDocument indexJobPost(JobPostDocument document);

    // Sync all job posts for a specific employer
    void syncJobPostsByEmployerId(String employerId);

    // Delete a job post from index
    void deleteJobPost(String id);

    // Multi-field keyword search
    Page<JobPostDocument> searchByKeywords(String keywords, Pageable pageable);

    // Advanced search with filters
    Page<JobPostDocument> advancedSearch(
            String keywords,
            JobStatus jobStatus,
            JobType jobType,
            String location,
            Double minSalary,
            Double maxSalary,
            String experience,
            Long categoryId,
            String employerId,
            LocalDate postedAfter,
            LocalDate closingBefore,
            Pageable pageable
    );

    // Sync job post from database to Elasticsearch
    void syncJobPostFromDatabase(String jobPostId);

    // Sync all job posts from database
    void syncAllJobPostsFromDatabase();

    // Delete all job posts and resync from database
    void deleteAllAndResync();
}
