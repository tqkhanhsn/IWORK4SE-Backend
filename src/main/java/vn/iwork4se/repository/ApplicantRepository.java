package vn.iwork4se.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iwork4se.model.Applicant;

import java.util.List;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, String> {
    @Query("SELECT a.id FROM Applicant a")
    List<String> findAllIds();

    @Query("SELECT DISTINCT a FROM Applicant a LEFT JOIN a.certificates c WHERE a.userStatus = 'ACTIVE' AND (" +
            "lower(a.firstName) LIKE lower(CONCAT('%', :keyword, '%')) OR " +
            "lower(a.lastName) LIKE lower(CONCAT('%', :keyword, '%')) OR " +
            "lower(a.userName) LIKE lower(CONCAT('%', :keyword, '%')) OR " +
            "lower(a.email) LIKE lower(CONCAT('%', :keyword, '%')) OR " +
            "lower(a.phone) LIKE lower(CONCAT('%', :keyword, '%')) OR " +
            "lower(a.address) LIKE lower(CONCAT('%', :keyword, '%')) OR " +
            "lower(a.careerObjective) LIKE lower(CONCAT('%', :keyword, '%')) OR " +
            "lower(a.universityName) LIKE lower(CONCAT('%', :keyword, '%')) OR " +
            "lower(a.major) LIKE lower(CONCAT('%', :keyword, '%')) OR " +
            "EXISTS (SELECT 1 FROM a.skills s WHERE lower(s) LIKE lower(CONCAT('%', :keyword, '%'))) OR " +
            "lower(c.certificateName) LIKE lower(CONCAT('%', :keyword, '%')) OR " +
            "lower(c.issuingOrganization) LIKE lower(CONCAT('%', :keyword, '%')) OR " +
            "lower(c.notes) LIKE lower(CONCAT('%', :keyword, '%')))")
    Page<Applicant> searchByKeywords(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT a FROM Applicant a WHERE a.userStatus = 'ACTIVE' AND a.yearsOfExperience >= :minExperience")
    Page<Applicant> findByMinExperience(@Param("minExperience") Integer minExperience, Pageable pageable);

    @Query("SELECT a FROM Applicant a WHERE a.userStatus = 'ACTIVE' AND a.gpa >= :minGpa")
    Page<Applicant> findByMinGpa(@Param("minGpa") Double minGpa, Pageable pageable);

    @Query("SELECT DISTINCT a FROM Applicant a JOIN a.skills s WHERE a.userStatus = 'ACTIVE' AND lower(s) LIKE lower(CONCAT('%', :skill, '%'))")
    Page<Applicant> findBySkill(@Param("skill") String skill, Pageable pageable);

    @Query("SELECT a FROM Applicant a WHERE a.userStatus = 'ACTIVE' AND lower(a.major) LIKE lower(CONCAT('%', :major, '%'))")
    Page<Applicant> findByMajor(@Param("major") String major, Pageable pageable);

    @Query("SELECT a FROM Applicant a WHERE a.userStatus = 'ACTIVE' AND lower(a.universityName) LIKE lower(CONCAT('%', :university, '%'))")
    Page<Applicant> findByUniversity(@Param("university") String university, Pageable pageable);
}
