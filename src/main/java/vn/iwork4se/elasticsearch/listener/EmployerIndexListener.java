package vn.iwork4se.elasticsearch.listener;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import vn.iwork4se.elasticsearch.service.JobPostSearchService;
import vn.iwork4se.model.Employer;

@Slf4j
@Component
public class EmployerIndexListener {

    private static JobPostSearchService jobPostSearchService;
    private static ApplicationEventPublisher eventPublisher;

    @Autowired
    public void setJobPostSearchService(JobPostSearchService service) {
        EmployerIndexListener.jobPostSearchService = service;
    }

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher publisher) {
        EmployerIndexListener.eventPublisher = publisher;
    }

    @PostUpdate
    public void onPostUpdate(Employer employer) {
        log.info("Employer entity updated, publishing event for job post sync: {}", employer.getId());
        try {
            if (eventPublisher != null) {
                // Publish event with just the employer ID to avoid lazy loading issues
                eventPublisher.publishEvent(new EmployerUpdatedEvent(employer.getId()));
            }
        } catch (Exception e) {
            log.error("Error publishing employer update event", e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEmployerUpdated(EmployerUpdatedEvent event) {
        log.info("Handling employer update event, syncing job posts for employer: {}", event.getEmployerId());
        try {
            if (jobPostSearchService != null) {
                // Use EntityManager to query job post IDs directly without accessing lazy collections
                jobPostSearchService.syncJobPostsByEmployerId(event.getEmployerId());
            }
        } catch (Exception e) {
            log.error("Error syncing employer's job posts to Elasticsearch", e);
        }
    }

    public static class EmployerUpdatedEvent {
        private final String employerId;

        public EmployerUpdatedEvent(String employerId) {
            this.employerId = employerId;
        }

        public String getEmployerId() {
            return employerId;
        }
    }
}
