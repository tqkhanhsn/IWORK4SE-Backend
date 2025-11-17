package vn.iwork4se.service;

import vn.iwork4se.controller.request.NotificationCreationRequest;
import vn.iwork4se.controller.response.NotificationCreationResponse;
import vn.iwork4se.controller.response.NotificationPageResponse;
import vn.iwork4se.controller.response.NotificationResponse;
import vn.iwork4se.model.Notification;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationService {
    // CRUD methods for Notification entity
    NotificationCreationResponse save(NotificationCreationRequest request);
    void deleteNotification(String id);
    Notification getNotificationById(String id);
    NotificationResponse findNotificationById(String id);
    
    // Business methods
    NotificationPageResponse findNotificationsByUser(String userId, int page, int size);
    NotificationPageResponse findNotificationsByUserOrderByDate(String userId, int page, int size);
    NotificationPageResponse findNotificationsByType(String type, int page, int size);
    NotificationPageResponse findNotificationsByUserAndType(String userId, String type, int page, int size);
    NotificationPageResponse findNotificationsByApplication(String applicationId, int page, int size);
    NotificationPageResponse findNotificationsByJobPost(String jobPostId, int page, int size);
    NotificationPageResponse findNotificationsByDateRange(LocalDateTime startDate, LocalDateTime endDate, int page, int size);
    NotificationPageResponse findNotificationsByUserAndDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate, int page, int size);
    NotificationPageResponse findNotificationsByMultipleCriteria(String userId, String type, String applicationId,
                                                                 String jobPostId,
                                                                 LocalDateTime startDate, LocalDateTime endDate, int page, int size);
    
    // Utility methods
    List<NotificationResponse> findRecentNotificationsByUser(String userId);
    long countNotificationsByUser(String userId);
    long countNotificationsByType(String type);
    
    // Notification creation helpers
    NotificationCreationResponse createApplicationStatusNotification(String userId, String applicationId, String message);
    NotificationCreationResponse createJobMatchNotification(String userId, String message);
    NotificationCreationResponse createJobPostStatusNotification(String userId, String jobPostId, String message);
    NotificationCreationResponse createUserStatusNotification(String userId, String message);
    NotificationCreationResponse createSystemNotification(String userId, String message);
    
    // Bulk operations
    void deleteNotificationsByUser(String userId);
    void deleteOldNotifications(LocalDateTime beforeDate);
}
