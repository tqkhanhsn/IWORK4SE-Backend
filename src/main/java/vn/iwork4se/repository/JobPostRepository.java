package vn.iwork4se.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iwork4se.common.JobStatus;
import vn.iwork4se.common.JobType;
import vn.iwork4se.model.JobPost;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, String> {
    
    // Find job posts by keyword (search in title, description, jobPosition, location)
    @Query("SELECT jp FROM JobPost jp WHERE " +
           "LOWER(jp.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(jp.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(jp.jobPosition) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(jp.location) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<JobPost> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // Find job posts by status
    Page<JobPost> findByJobStatus(JobStatus jobStatus, Pageable pageable);
    
    // Find job posts by job type
    Page<JobPost> findByJobType(JobType jobType, Pageable pageable);
    
    // Find job posts by location
    Page<JobPost> findByLocationContainingIgnoreCase(String location, Pageable pageable);
    
    // Find job posts by employer
    Page<JobPost> findByEmployerId(String employerId, Pageable pageable);
    
    // Find job posts by salary range
    @Query("SELECT jp FROM JobPost jp WHERE jp.minSalary >= :minSalary AND jp.maxSalary <= :maxSalary")
    Page<JobPost> findBySalaryRange(@Param("minSalary") Double minSalary, @Param("maxSalary") Double maxSalary, Pageable pageable);
    
    // Find job posts by experience
    Page<JobPost> findByExperienceContainingIgnoreCase(String experience, Pageable pageable);
    
    // Find active job posts (not expired)
    @Query("SELECT jp FROM JobPost jp WHERE jp.jobStatus = :status AND jp.closingDate >= :currentDate")
    Page<JobPost> findActiveJobPosts(@Param("status") JobStatus status, @Param("currentDate") LocalDate currentDate, Pageable pageable);
    
    // Find job posts by multiple criteria
    @Query("SELECT jp FROM JobPost jp WHERE " +
           "(:keyword IS NULL OR LOWER(jp.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(jp.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(jp.jobPosition) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(jp.location) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:jobStatus IS NULL OR jp.jobStatus = :jobStatus) AND " +
           "(:jobType IS NULL OR jp.jobType = :jobType) AND " +
           "(:location IS NULL OR LOWER(jp.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:minSalary IS NULL OR jp.minSalary >= :minSalary) AND " +
           "(:maxSalary IS NULL OR jp.maxSalary <= :maxSalary)")
    Page<JobPost> findByMultipleCriteria(@Param("keyword") String keyword,
                                        @Param("jobStatus") JobStatus jobStatus,
                                        @Param("jobType") JobType jobType,
                                        @Param("location") String location,
                                        @Param("minSalary") Double minSalary,
                                        @Param("maxSalary") Double maxSalary,
                                        Pageable pageable);
    
    // Find job posts by category
    Page<JobPost> findByCategoryId(Long categoryId, Pageable pageable);
    
    // Count job posts by employer
    long countByEmployerId(String employerId);
    
    // Find job posts expiring soon (within next 7 days)
    @Query("SELECT jp FROM JobPost jp WHERE jp.closingDate BETWEEN :today AND :nextWeek")
    List<JobPost> findJobPostsExpiringSoon(@Param("today") LocalDate today, @Param("nextWeek") LocalDate nextWeek);
}
