package vn.iwork4se.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iwork4se.model.Notification;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    
    // Find notifications by user ID
    Page<Notification> findByUserId(String userId, Pageable pageable);
    
    // Find notifications by user ID ordered by creation date (newest first)
    Page<Notification> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    
    // Find notifications by type
    Page<Notification> findByType(String type, Pageable pageable);
    
    // Find notifications by user and type
    Page<Notification> findByUserIdAndType(String userId, String type, Pageable pageable);
    
    // Find notifications by application ID
    Page<Notification> findByApplicationId(String applicationId, Pageable pageable);

    // Find notifications by job post ID
    Page<Notification> findByJobPostId(String jobPostId, Pageable pageable);
    
    // Find notifications by date range
    @Query("SELECT n FROM Notification n WHERE n.createdAt BETWEEN :startDate AND :endDate")
    Page<Notification> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate, 
                                      Pageable pageable);
    
    // Find notifications by user and date range
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.createdAt BETWEEN :startDate AND :endDate")
    Page<Notification> findByUserIdAndDateRange(@Param("userId") String userId,
                                               @Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate, 
                                               Pageable pageable);
    
    // Count notifications by user
    long countByUserId(String userId);
    
    // Count notifications by type
    long countByType(String type);
    
    // Find recent notifications for user (last 7 days)
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.createdAt >= :sevenDaysAgo ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotificationsByUser(@Param("userId") String userId, 
                                                    @Param("sevenDaysAgo") LocalDateTime sevenDaysAgo);
    
    // Find notifications by multiple criteria
    @Query("SELECT n FROM Notification n WHERE " +
           "(:userId IS NULL OR n.user.id = :userId) AND " +
           "(:type IS NULL OR n.type = :type) AND " +
           "(:applicationId IS NULL OR n.application.id = :applicationId) AND " +
           "(:jobPostId IS NULL OR n.jobPost.id = :jobPostId) AND " +
           "(:startDate IS NULL OR n.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR n.createdAt <= :endDate)")
    Page<Notification> findByMultipleCriteria(@Param("userId") String userId,
                                            @Param("type") String type,
                                            @Param("applicationId") String applicationId,
                                            @Param("jobPostId") String jobPostId,
                                            @Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate,
                                            Pageable pageable);
}
