package vn.iwork4se.elasticsearch.listener;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.iwork4se.elasticsearch.service.ApplicantSearchService;
import vn.iwork4se.model.SavedApplicant;

@Slf4j
@Component
public class SavedApplicantIndexListener {

    private static ApplicantSearchService applicantSearchService;

    @Autowired
    public void setApplicantSearchService(ApplicantSearchService service) {
        SavedApplicantIndexListener.applicantSearchService = service;
    }

    @PostPersist
    @PostUpdate
    public void onSavedApplicantChange(SavedApplicant savedApplicant) {
        log.info("SavedApplicant entity changed, syncing related applicant to Elasticsearch");
        try {
            if (applicantSearchService != null && savedApplicant.getApplicant() != null) {
                // Sync the applicant to update savedInListIds
                applicantSearchService.syncApplicantFromDatabase(savedApplicant.getApplicant().getId());
            }
        } catch (Exception e) {
            log.error("Error syncing applicant after SavedApplicant change: {}", e.getMessage());
        }
    }

    @PostRemove
    public void onSavedApplicantRemove(SavedApplicant savedApplicant) {
        log.info("SavedApplicant entity removed, syncing related applicant to Elasticsearch");
        try {
            if (applicantSearchService != null && savedApplicant.getApplicant() != null) {
                // Sync the applicant to update savedInListIds
                applicantSearchService.syncApplicantFromDatabase(savedApplicant.getApplicant().getId());
            }
        } catch (Exception e) {
            log.error("Error syncing applicant after SavedApplicant removal: {}", e.getMessage());
        }
    }
}
