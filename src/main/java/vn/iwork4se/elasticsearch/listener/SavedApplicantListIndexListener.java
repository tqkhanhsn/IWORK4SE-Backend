package vn.iwork4se.elasticsearch.listener;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.iwork4se.elasticsearch.service.ApplicantSearchService;
import vn.iwork4se.model.SavedApplicant;
import vn.iwork4se.model.SavedApplicantList;

@Slf4j
@Component
public class SavedApplicantListIndexListener {

    private static ApplicantSearchService applicantSearchService;

    @Autowired
    public void setApplicantSearchService(ApplicantSearchService service) {
        SavedApplicantListIndexListener.applicantSearchService = service;
    }

    @PreRemove
    public void onSavedApplicantListRemove(SavedApplicantList savedApplicantList) {
        log.info("SavedApplicantList entity being removed, syncing all related applicants to Elasticsearch");
        try {
            if (applicantSearchService != null && savedApplicantList.getSavedApplicants() != null) {
                // Sync all applicants in this list to update their savedInListIds
                for (SavedApplicant savedApplicant : savedApplicantList.getSavedApplicants()) {
                    if (savedApplicant.getApplicant() != null) {
                        applicantSearchService.syncApplicantFromDatabase(savedApplicant.getApplicant().getId());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error syncing applicants after SavedApplicantList removal: {}", e.getMessage());
        }
    }
}
