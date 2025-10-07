package vn.iwork4se.elasticsearch.listener;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.iwork4se.elasticsearch.service.ApplicantSearchService;
import vn.iwork4se.model.Applicant;
import vn.iwork4se.model.User;

@Slf4j
@Component
public class UserIndexListener {

    private static ApplicantSearchService applicantSearchService;

    @Autowired
    public void setApplicantSearchService(ApplicantSearchService service) {
        UserIndexListener.applicantSearchService = service;
    }

    @PostUpdate
    public void onPostUpdate(User user) {

        log.info("User entity updated, checking if applicant needs sync: {}", user.getId());
        try {
            // If this user is an applicant, sync to Elasticsearch
            if (applicantSearchService != null && user instanceof Applicant) {
                applicantSearchService.syncApplicantFromDatabase(user.getId());
            }
        } catch (Exception e) {
            log.error("Error syncing user/applicant to Elasticsearch: {}", e.getMessage());
        }
    }
}
