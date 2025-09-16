package vn.iwork4se.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.iwork4se.model.Certificate;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Certificate c WHERE c.applicant.id = :applicantId")
    void deleteByApplicantId(@Param("applicantId") String applicantId);
}
