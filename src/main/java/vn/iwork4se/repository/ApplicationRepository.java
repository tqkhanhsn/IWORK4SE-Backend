package vn.iwork4se.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iwork4se.common.ApplicationStatus;
import vn.iwork4se.model.Application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, String> {
    
    // Find applications by applicant ID
    Page<Application> findByApplicantId(String applicantId, Pageable pageable);
    
    // Find applications by job post ID
    Page<Application> findByJobId(String jobId, Pageable pageable);
    
    // Find applications by status
    Page<Application> findByApplicationStatus(ApplicationStatus status, Pageable pageable);
    
    // Find applications by applicant and status
    Page<Application> findByApplicantIdAndApplicationStatus(String applicantId, ApplicationStatus status, Pageable pageable);
    
    // Find applications by job and status
    Page<Application> findByJobIdAndApplicationStatus(String jobId, ApplicationStatus status, Pageable pageable);
    
    // Check if applicant already applied for a job
    Optional<Application> findByApplicantIdAndJobId(String applicantId, String jobId);
    
    // Find applications by date range
    @Query("SELECT a FROM Application a WHERE a.appliedAt BETWEEN :startDate AND :endDate")
    Page<Application> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate, 
                                     Pageable pageable);
    
    // Find applications by employer (through job post)
    @Query("SELECT a FROM Application a WHERE a.job.employer.id = :employerId")
    Page<Application> findByEmployerId(@Param("employerId") String employerId, Pageable pageable);

    // Find applications by employer with optional status filter
    @Query("""
            SELECT a FROM Application a
            WHERE a.job.employer.id = :employerId
            AND (:status IS NULL OR a.applicationStatus = :status)
            """)
    Page<Application> findByEmployerIdAndStatus(
            @Param("employerId") String employerId,
            @Param("status") ApplicationStatus status,
            Pageable pageable);
    
    // Count applications by applicant
    long countByApplicantId(String applicantId);
    
    // Count applications by job post
    long countByJobId(String jobId);
    
    // Count applications by status
    long countByApplicationStatus(ApplicationStatus status);
    
    // Find applications with CV details
    @Query("SELECT a FROM Application a JOIN FETCH a.cv WHERE a.applicant.id = :applicantId")
    List<Application> findByApplicantIdWithCV(@Param("applicantId") String applicantId);
    
    // Find recent applications (last 30 days)
    @Query("SELECT a FROM Application a WHERE a.appliedAt >= :thirtyDaysAgo")
    Page<Application> findRecentApplications(@Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo, Pageable pageable);
    
    // Find applications by multiple criteria
    @Query("SELECT a FROM Application a WHERE " +
           "(:applicantId IS NULL OR a.applicant.id = :applicantId) AND " +
           "(:jobId IS NULL OR a.job.id = :jobId) AND " +
           "(:status IS NULL OR a.applicationStatus = :status) AND " +
           "(:startDate IS NULL OR a.appliedAt >= :startDate) AND " +
           "(:endDate IS NULL OR a.appliedAt <= :endDate)")
    Page<Application> findByMultipleCriteria(@Param("applicantId") String applicantId,
                                           @Param("jobId") String jobId,
                                           @Param("status") ApplicationStatus status,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate,
                                           Pageable pageable);
}
