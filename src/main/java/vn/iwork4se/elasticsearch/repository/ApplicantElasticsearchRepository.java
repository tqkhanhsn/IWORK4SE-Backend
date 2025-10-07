package vn.iwork4se.elasticsearch.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import vn.iwork4se.common.UserStatus;
import vn.iwork4se.elasticsearch.document.ApplicantDocument;

@Repository
public interface ApplicantElasticsearchRepository extends ElasticsearchRepository<ApplicantDocument, String> {

    // Find by user status
    Page<ApplicantDocument> findByUserStatus(UserStatus userStatus, Pageable pageable);

    // Find by minimum experience
    @Query("{\"bool\": {\"must\": [{\"term\": {\"userStatus\": \"ACTIVE\"}}, {\"range\": {\"yearsOfExperience\": {\"gte\": ?0}}}]}}")
    Page<ApplicantDocument> findByMinExperience(Integer minExperience, Pageable pageable);

    // Find by minimum GPA
    @Query("{\"bool\": {\"must\": [{\"term\": {\"userStatus\": \"ACTIVE\"}}, {\"range\": {\"gpa\": {\"gte\": ?0}}}]}}")
    Page<ApplicantDocument> findByMinGpa(Double minGpa, Pageable pageable);
}
