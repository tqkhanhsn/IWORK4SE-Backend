package vn.iwork4se.elasticsearch.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.iwork4se.common.Gender;
import vn.iwork4se.common.UserStatus;
import vn.iwork4se.elasticsearch.document.ApplicantDocument;

public interface ApplicantSearchService {

    // Index an applicant
    ApplicantDocument indexApplicant(ApplicantDocument document);

    // Delete an applicant from index
    void deleteApplicant(String id);

    // Multi-field keyword search
    Page<ApplicantDocument> searchByKeywords(String keywords, Pageable pageable);

    // Advanced search with filters
    Page<ApplicantDocument> advancedSearch(
            String keywords,
            Integer minExperience,
            Double minGpa,
            String skill,
            String major,
            String university,
            Gender gender,
            UserStatus userStatus,
            String savedApplicantListId,
            Pageable pageable
    );

    // Sync applicant from database to Elasticsearch
    void syncApplicantFromDatabase(String applicantId);

    // Sync all applicants from database
    void syncAllApplicantsFromDatabase();

    // Delete all applicants and resync from database
    void deleteAllAndResync();
}
