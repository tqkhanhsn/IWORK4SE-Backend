package vn.iwork4se.elasticsearch.listener;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.iwork4se.elasticsearch.service.JobPostSearchService;
import vn.iwork4se.model.Employer;
import vn.iwork4se.model.JobPost;

@Slf4j
@Component
public class EmployerIndexListener {

    private static JobPostSearchService jobPostSearchService;

    @Autowired
    public void setJobPostSearchService(JobPostSearchService service) {
        EmployerIndexListener.jobPostSearchService = service;
    }

    @PostUpdate
    public void onPostUpdate(Employer employer) {

        log.info("Employer entity updated, syncing related job posts to Elasticsearch: {}", employer.getId());
        try {
            if (jobPostSearchService != null && employer.getJobPosts() != null) {
                // Sync all job posts of this employer
                for (JobPost jobPost : employer.getJobPosts()) {
                    jobPostSearchService.syncJobPostFromDatabase(jobPost.getId());
                }
            }
        } catch (Exception e) {
            log.error("Error syncing employer's job posts to Elasticsearch: {}", e.getMessage());
        }
    }
}
