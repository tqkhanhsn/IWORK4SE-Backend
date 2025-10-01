package vn.iwork4se.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iwork4se.model.CV;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CVRepository extends JpaRepository<CV, String> {
    
    // Find CVs by applicant ID
    Page<CV> findByApplicantId(String applicantId, Pageable pageable);
    
    // Find CVs by applicant ID ordered by upload date (newest first)
    Page<CV> findByApplicantIdOrderByUploadedDateDesc(String applicantId, Pageable pageable);
    
    // Find CVs by date range
    @Query("SELECT cv FROM CV cv WHERE cv.uploadedDate BETWEEN :startDate AND :endDate")
    Page<CV> findByDateRange(@Param("startDate") LocalDate startDate, 
                            @Param("endDate") LocalDate endDate, 
                            Pageable pageable);
    
    // Find CVs by applicant and date range
    @Query("SELECT cv FROM CV cv WHERE cv.applicant.id = :applicantId AND cv.uploadedDate BETWEEN :startDate AND :endDate")
    Page<CV> findByApplicantIdAndDateRange(@Param("applicantId") String applicantId,
                                          @Param("startDate") LocalDate startDate, 
                                          @Param("endDate") LocalDate endDate, 
                                          Pageable pageable);
    
    // Count CVs by applicant
    long countByApplicantId(String applicantId);
    
    // Find latest CV by applicant
    @Query("SELECT cv FROM CV cv WHERE cv.applicant.id = :applicantId ORDER BY cv.uploadedDate DESC")
    List<CV> findLatestCVByApplicant(@Param("applicantId") String applicantId, Pageable pageable);
    
    // Find CVs used in applications
    @Query("SELECT cv FROM CV cv WHERE cv.application IS NOT NULL")
    Page<CV> findCVsUsedInApplications(Pageable pageable);
    
    // Find unused CVs by applicant
    @Query("SELECT cv FROM CV cv WHERE cv.applicant.id = :applicantId AND cv.application IS NULL")
    List<CV> findUnusedCVsByApplicant(@Param("applicantId") String applicantId);
    
    // Find recent CVs (last 30 days)
    @Query("SELECT cv FROM CV cv WHERE cv.uploadedDate >= :thirtyDaysAgo")
    Page<CV> findRecentCVs(@Param("thirtyDaysAgo") LocalDate thirtyDaysAgo, Pageable pageable);
    
    // Find CVs by URL pattern (for file management)
    @Query("SELECT cv FROM CV cv WHERE cv.url LIKE %:urlPattern%")
    List<CV> findByUrlPattern(@Param("urlPattern") String urlPattern);
}
