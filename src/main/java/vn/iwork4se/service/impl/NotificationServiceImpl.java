package vn.iwork4se.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import vn.iwork4se.controller.request.NotificationCreationRequest;
import vn.iwork4se.controller.response.NotificationCreationResponse;
import vn.iwork4se.controller.response.NotificationPageResponse;
import vn.iwork4se.controller.response.NotificationResponse;
import vn.iwork4se.exception.ResourceNotFoundException;
import vn.iwork4se.model.Application;
import vn.iwork4se.model.JobPost;
import vn.iwork4se.model.Notification;
import vn.iwork4se.model.User;
import vn.iwork4se.repository.ApplicationRepository;
import vn.iwork4se.repository.JobPostRepository;
import vn.iwork4se.repository.NotificationRepository;
import vn.iwork4se.repository.UserRepository;
import vn.iwork4se.service.NotificationService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final JobPostRepository jobPostRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public NotificationCreationResponse save(NotificationCreationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Application application = null;
        if (request.getApplicationId() != null) {
            application = applicationRepository.findById(request.getApplicationId())
                    .orElseThrow(() -> new RuntimeException("Application not found"));
        }

        JobPost jobPost = null;
        if (request.getJobPostId() != null) {
            jobPost = jobPostRepository.findById(request.getJobPostId())
                    .orElseThrow(() -> new RuntimeException("Job post not found"));
        }

        Notification notification = Notification.builder()
                .id("NOT" + UUID.randomUUID().toString())
                .user(user)
                .application(application)
                .jobPost(jobPost)
                .isRead(false)
                .type(request.getType())
                .message(request.getMessage())
                .createdAt(LocalDateTime.now())
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification created successfully, notificationId={}", savedNotification.getId());

        // Send realtime notification via WebSocket
        sendRealtimeNotification(savedNotification);

        return NotificationCreationResponse.builder()
                .id(savedNotification.getId())
                .userId(savedNotification.getUser().getId())
                .applicationId(savedNotification.getApplication() != null ? savedNotification.getApplication().getId() : null)
                .jobPostId(savedNotification.getJobPost() != null ? savedNotification.getJobPost().getId() : null)
                .type(savedNotification.getType())
                .message(savedNotification.getMessage())
                .isRead(savedNotification.isRead())
                .createdAt(savedNotification.getCreatedAt())
                .build();
    }

    private void sendRealtimeNotification(Notification notification) {
        try {
            User user = notification.getUser();
            String userId = user.getId();
            String userType = user.getUserType().toString();

            // Convert notification to response format
            NotificationResponse notificationResponse = convertToNotificationResponse(notification);

            // Create notification payload
            Map<String, Object> notificationPayload = new HashMap<>();
            notificationPayload.put("id", notificationResponse.getId());
            notificationPayload.put("userId", notificationResponse.getUserId());
            notificationPayload.put("userName", notificationResponse.getUserName());
            notificationPayload.put("applicationId", notificationResponse.getApplicationId());
            notificationPayload.put("jobPostId", notificationResponse.getJobPostId());
            notificationPayload.put("type", notificationResponse.getType());
            notificationPayload.put("message", notificationResponse.getMessage());
            notificationPayload.put("isRead", notificationResponse.isRead());
            notificationPayload.put("createdAt", notificationResponse.getCreatedAt().toString());
            notificationPayload.put("timestamp", System.currentTimeMillis());

            // Determine topic based on user type
            String topic;
            if ("APPLICANT".equals(userType)) {
                topic = "/topic/notifications/applicant/" + userId;
            } else if ("EMPLOYER".equals(userType)) {
                topic = "/topic/notifications/employer/" + userId;
            } else if ("ADMIN".equals(userType)) {
                topic = "/topic/notifications/admin/" + userId;
            } else {
                // Fallback to general notification topic
                topic = "/topic/notifications/user/" + userId;
            }

            log.debug("[SOCKET] Broadcasting notification to topic: {}", topic);
            messagingTemplate.convertAndSend(topic, notificationPayload);
            log.info("[SOCKET] Notification {} sent successfully to user: {} via topic: {}", 
                    notification.getId(), userId, topic);
        } catch (Exception e) {
            log.error("[SOCKET] Error sending realtime notification for notification: {}", 
                    notification.getId(), e);
            // Don't throw exception - notification is already saved to database
        }
    }

    @Override
    public void deleteNotification(String id) {
        Notification notification = getNotificationById(id);
        notificationRepository.delete(notification);
        log.info("Notification deleted successfully, notificationId={}", id);
    }

    @Override
    public Notification getNotificationById(String id) {
        return notificationRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Notification not found with id: " + id));
    }

    @Override
    public NotificationResponse findNotificationById(String id) {
        Notification notification = getNotificationById(id);
        return convertToNotificationResponse(notification);
    }

    @Override
    public NotificationPageResponse findNotificationsByUser(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> notificationPage = notificationRepository.findByUserId(userId, pageable);
        
        List<NotificationResponse> notificationResponses = notificationPage.getContent().stream()
                .map(this::convertToNotificationResponse)
                .collect(Collectors.toList());
        
        return new NotificationPageResponse(
                notificationResponses,
                notificationPage.getNumber(),
                notificationPage.getSize(),
                notificationPage.getTotalPages(),
                notificationPage.getTotalElements()
        );
    }

    @Override
    public NotificationPageResponse findNotificationsByUserOrderByDate(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> notificationPage = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        
        List<NotificationResponse> notificationResponses = notificationPage.getContent().stream()
                .map(this::convertToNotificationResponse)
                .collect(Collectors.toList());
        
        return new NotificationPageResponse(
                notificationResponses,
                notificationPage.getNumber(),
                notificationPage.getSize(),
                notificationPage.getTotalPages(),
                notificationPage.getTotalElements()
        );
    }

    @Override
    public NotificationPageResponse findNotificationsByType(String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> notificationPage = notificationRepository.findByType(type, pageable);
        
        List<NotificationResponse> notificationResponses = notificationPage.getContent().stream()
                .map(this::convertToNotificationResponse)
                .collect(Collectors.toList());
        
        return new NotificationPageResponse(
                notificationResponses,
                notificationPage.getNumber(),
                notificationPage.getSize(),
                notificationPage.getTotalPages(),
                notificationPage.getTotalElements()
        );
    }

    @Override
    public NotificationPageResponse findNotificationsByUserAndType(String userId, String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> notificationPage = notificationRepository.findByUserIdAndType(userId, type, pageable);
        
        List<NotificationResponse> notificationResponses = notificationPage.getContent().stream()
                .map(this::convertToNotificationResponse)
                .collect(Collectors.toList());
        
        return new NotificationPageResponse(
                notificationResponses,
                notificationPage.getNumber(),
                notificationPage.getSize(),
                notificationPage.getTotalPages(),
                notificationPage.getTotalElements()
        );
    }

    @Override
    public NotificationPageResponse findNotificationsByApplication(String applicationId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> notificationPage = notificationRepository.findByApplicationId(applicationId, pageable);
        
        List<NotificationResponse> notificationResponses = notificationPage.getContent().stream()
                .map(this::convertToNotificationResponse)
                .collect(Collectors.toList());
        
        return new NotificationPageResponse(
                notificationResponses,
                notificationPage.getNumber(),
                notificationPage.getSize(),
                notificationPage.getTotalPages(),
                notificationPage.getTotalElements()
        );
    }

    @Override
    public NotificationPageResponse findNotificationsByJobPost(String jobPostId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> notificationPage = notificationRepository.findByJobPostId(jobPostId, pageable);

        List<NotificationResponse> notificationResponses = notificationPage.getContent().stream()
                .map(this::convertToNotificationResponse)
                .collect(Collectors.toList());

        return new NotificationPageResponse(
                notificationResponses,
                notificationPage.getNumber(),
                notificationPage.getSize(),
                notificationPage.getTotalPages(),
                notificationPage.getTotalElements()
        );
    }

    @Override
    public NotificationPageResponse findNotificationsByDateRange(LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> notificationPage = notificationRepository.findByDateRange(startDate, endDate, pageable);
        
        List<NotificationResponse> notificationResponses = notificationPage.getContent().stream()
                .map(this::convertToNotificationResponse)
                .collect(Collectors.toList());
        
        return new NotificationPageResponse(
                notificationResponses,
                notificationPage.getNumber(),
                notificationPage.getSize(),
                notificationPage.getTotalPages(),
                notificationPage.getTotalElements()
        );
    }

    @Override
    public NotificationPageResponse findNotificationsByUserAndDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> notificationPage = notificationRepository.findByUserIdAndDateRange(userId, startDate, endDate, pageable);
        
        List<NotificationResponse> notificationResponses = notificationPage.getContent().stream()
                .map(this::convertToNotificationResponse)
                .collect(Collectors.toList());
        
        return new NotificationPageResponse(
                notificationResponses,
                notificationPage.getNumber(),
                notificationPage.getSize(),
                notificationPage.getTotalPages(),
                notificationPage.getTotalElements()
        );
    }

    @Override
    public NotificationPageResponse findNotificationsByMultipleCriteria(String userId, String type, String applicationId,
                                                                       String jobPostId,
                                                                       LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> notificationPage = notificationRepository.findByMultipleCriteria(
                userId, type, applicationId, jobPostId, startDate, endDate, pageable);
        
        List<NotificationResponse> notificationResponses = notificationPage.getContent().stream()
                .map(this::convertToNotificationResponse)
                .collect(Collectors.toList());
        
        return new NotificationPageResponse(
                notificationResponses,
                notificationPage.getNumber(),
                notificationPage.getSize(),
                notificationPage.getTotalPages(),
                notificationPage.getTotalElements()
        );
    }

    @Override
    public List<NotificationResponse> findRecentNotificationsByUser(String userId) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Notification> notifications = notificationRepository.findRecentNotificationsByUser(userId, sevenDaysAgo);
        
        return notifications.stream()
                .map(this::convertToNotificationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public long countNotificationsByUser(String userId) {
        return notificationRepository.countByUserId(userId);
    }

    @Override
    public long countNotificationsByType(String type) {
        return notificationRepository.countByType(type);
    }

    @Override
    public NotificationCreationResponse createApplicationStatusNotification(String userId, String applicationId, String message) {
        NotificationCreationRequest request = NotificationCreationRequest.builder()
                .userId(userId)
                .applicationId(applicationId)
                .type("APPLICATION_STATUS")
                .message(message)
                .build();
        return save(request);
    }

    @Override
    public NotificationCreationResponse createJobMatchNotification(String userId, String message) {
        NotificationCreationRequest request = NotificationCreationRequest.builder()
                .userId(userId)
                .type("JOB_MATCH")
                .message(message)
                .build();
        return save(request);
    }

    @Override
    public NotificationCreationResponse createSystemNotification(String userId, String message) {
        NotificationCreationRequest request = NotificationCreationRequest.builder()
                .userId(userId)
                .type("SYSTEM")
                .message(message)
                .build();
        return save(request);
    }

    @Override
    public NotificationCreationResponse createJobPostStatusNotification(String userId, String jobPostId, String message) {
        NotificationCreationRequest request = NotificationCreationRequest.builder()
                .userId(userId)
                .jobPostId(jobPostId)
                .type("JOB_POST_STATUS")
                .message(message)
                .build();
        return save(request);
    }

    @Override
    public NotificationCreationResponse createUserStatusNotification(String userId, String message) {
        NotificationCreationRequest request = NotificationCreationRequest.builder()
                .userId(userId)
                .type("USER_STATUS")
                .message(message)
                .build();
        return save(request);
    }

    @Override
    public void deleteNotificationsByUser(String userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId, Pageable.unpaged()).getContent();
        notificationRepository.deleteAll(notifications);
        log.info("All notifications deleted for user, userId={}", userId);
    }

    @Override
    public void deleteOldNotifications(LocalDateTime beforeDate) {
        Page<Notification> oldNotifications = notificationRepository.findByDateRange(LocalDateTime.MIN, beforeDate, Pageable.unpaged());
        notificationRepository.deleteAll(oldNotifications.getContent());
        log.info("Old notifications deleted before date: {}", beforeDate);
    }

    private NotificationResponse convertToNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .userName(notification.getUser().getFirstName() + " " + notification.getUser().getLastName())
                .applicationId(notification.getApplication() != null ? notification.getApplication().getId() : null)
                .jobPostId(notification.getJobPost() != null ? notification.getJobPost().getId() : null)
                .type(notification.getType())
                .message(notification.getMessage())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
