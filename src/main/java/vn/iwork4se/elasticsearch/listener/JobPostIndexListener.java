package vn.iwork4se.elasticsearch.listener;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import vn.iwork4se.elasticsearch.service.JobPostSearchService;
import vn.iwork4se.model.Applicant;
import vn.iwork4se.model.JobPost;

@Slf4j
@Component
public class JobPostIndexListener {

    private static JobPostSearchService jobPostSearchService;

    @Autowired
    public void setJobPostSearchService(JobPostSearchService service) {
        JobPostIndexListener.jobPostSearchService = service;
    }

    @PostPersist
    @PostUpdate
    public void onPostPersistOrUpdate(JobPost jobPost) {
        log.info("JobPost entity changed, syncing to Elasticsearch: {}", jobPost.getId());
        // Trigger the actual sync after transaction commits
        syncAfterCommit(jobPost);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void syncAfterCommit(JobPost jobPost) {
        log.info("Transaction committed, syncing job post to Elasticsearch: {}", jobPost.getId());

        try {
            if (jobPostSearchService != null) {
                jobPostSearchService.syncJobPostFromDatabase(jobPost.getId());
            }
        } catch (Exception e) {
            log.error("Error syncing job post to Elasticsearch: {}", e.getMessage());
        }
    }

    @PreRemove
    public void onPreRemove(JobPost jobPost) {
        log.info("JobPost entity removed, deleting from Elasticsearch: {}", jobPost.getId());
        try {
            if (jobPostSearchService != null) {
                jobPostSearchService.deleteJobPost(jobPost.getId());
            }
        } catch (Exception e) {
            log.error("Error deleting job post from Elasticsearch: {}", e.getMessage());
        }
    }
}
