package vn.iwork4se.elasticsearch.listener;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.iwork4se.elasticsearch.service.ApplicantSearchService;
import vn.iwork4se.model.Certificate;

@Slf4j
@Component
public class CertificateIndexListener {

    private static ApplicantSearchService applicantSearchService;

    @Autowired
    public void setApplicantSearchService(ApplicantSearchService service) {
        CertificateIndexListener.applicantSearchService = service;
    }

    @PostPersist
    @PostUpdate
    @PostRemove
    public void onCertificateChange(Certificate certificate) {

        log.info("Certificate entity changed, syncing related applicant to Elasticsearch");
        try {
            if (applicantSearchService != null && certificate.getApplicant() != null) {
                // Sync the applicant who owns this certificate
                applicantSearchService.syncApplicantFromDatabase(certificate.getApplicant().getId());
            }
        } catch (Exception e) {
            log.error("Error syncing applicant after certificate change: {}", e.getMessage());
        }
    }
}
