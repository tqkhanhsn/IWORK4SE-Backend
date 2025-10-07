package vn.iwork4se.elasticsearch.listener;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;
import vn.iwork4se.elasticsearch.service.ApplicantSearchService;
import vn.iwork4se.model.Applicant;

@Slf4j
@Component
public class ApplicantIndexListener {

    private static ApplicantSearchService applicantSearchService;

    @Autowired
    public void setApplicantSearchService(ApplicantSearchService service) {
        ApplicantIndexListener.applicantSearchService = service;
    }

    @PostPersist
    @PostUpdate
    public void onPostPersistOrUpdate(Applicant applicant) {
        log.info("Applicant entity changed, scheduling sync to Elasticsearch: {}", applicant.getId());
        // Trigger the actual sync after transaction commits
        syncAfterCommit(applicant);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void syncAfterCommit(Applicant applicant) {
        log.info("Transaction committed, syncing applicant to Elasticsearch: {}", applicant.getId());

        try {
            if (applicantSearchService != null) {
                applicantSearchService.syncApplicantFromDatabase(applicant.getId());
            }
        } catch (Exception e) {
            log.error("Error syncing applicant to Elasticsearch: {}", e.getMessage());
        }
    }

    @PreRemove
    public void onPreRemove(Applicant applicant) {
        log.info("Applicant entity removed, deleting from Elasticsearch: {}", applicant.getId());
        try {
            if (applicantSearchService != null) {
                applicantSearchService.deleteApplicant(applicant.getId());
            }
        } catch (Exception e) {
            log.error("Error deleting applicant from Elasticsearch: {}", e.getMessage());
        }
    }
}
