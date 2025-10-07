package vn.iwork4se.elasticsearch.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import vn.iwork4se.common.JobStatus;
import vn.iwork4se.common.JobType;
import vn.iwork4se.elasticsearch.document.JobPostDocument;

import java.time.LocalDate;

@Repository
public interface JobPostElasticsearchRepository extends ElasticsearchRepository<JobPostDocument, String> {

    // Find by job status
    Page<JobPostDocument> findByJobStatus(JobStatus jobStatus, Pageable pageable);

    // Find by job type
    Page<JobPostDocument> findByJobType(JobType jobType, Pageable pageable);

    // Find by employer
    Page<JobPostDocument> findByEmployerId(String employerId, Pageable pageable);

    // Find by category
    Page<JobPostDocument> findByCategoryId(Long categoryId, Pageable pageable);

    // Find active jobs (not expired)
    @Query("{\"bool\": {\"must\": [{\"term\": {\"jobStatus\": \"?0\"}}, {\"range\": {\"closingDate\": {\"gte\": \"?1\"}}}]}}")
    Page<JobPostDocument> findActiveJobs(JobStatus status, LocalDate currentDate, Pageable pageable);
}
