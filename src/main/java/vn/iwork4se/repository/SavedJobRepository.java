package vn.iwork4se.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iwork4se.model.SavedJob;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJob, String> {
    
    // Find saved jobs by applicant ID
    Page<SavedJob> findByApplicantId(String applicantId, Pageable pageable);
    
    // Find saved jobs by job post ID
    Page<SavedJob> findByJobId(String jobId, Pageable pageable);
    
    // Check if a job is already saved by an applicant
    Optional<SavedJob> findByApplicantIdAndJobId(String applicantId, String jobId);
    
    // Find saved jobs by applicant and date range
    @Query("SELECT sj FROM SavedJob sj WHERE sj.applicant.id = :applicantId AND sj.savedDate BETWEEN :startDate AND :endDate")
    Page<SavedJob> findByApplicantIdAndDateRange(@Param("applicantId") String applicantId, 
                                                @Param("startDate") LocalDate startDate, 
                                                @Param("endDate") LocalDate endDate, 
                                                Pageable pageable);
    
    // Count saved jobs by applicant
    long countByApplicantId(String applicantId);
    
    // Count saved jobs by job post
    long countByJobId(String jobId);
    
    // Find saved jobs with job details by applicant
    @Query("SELECT sj FROM SavedJob sj JOIN FETCH sj.job WHERE sj.applicant.id = :applicantId")
    List<SavedJob> findByApplicantIdWithJobDetails(@Param("applicantId") String applicantId);
    
    // Find recent saved jobs by applicant (last 30 days)
    @Query("SELECT sj FROM SavedJob sj WHERE sj.applicant.id = :applicantId AND sj.savedDate >= :thirtyDaysAgo")
    Page<SavedJob> findRecentSavedJobsByApplicant(@Param("applicantId") String applicantId, 
                                                 @Param("thirtyDaysAgo") LocalDate thirtyDaysAgo, 
                                                 Pageable pageable);
}
