package vn.iwork4se.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.iwork4se.controller.request.NotificationCreationRequest;
import vn.iwork4se.controller.response.NotificationCreationResponse;
import vn.iwork4se.controller.response.NotificationPageResponse;
import vn.iwork4se.controller.response.NotificationResponse;
import vn.iwork4se.exception.ResourceNotFoundException;
import vn.iwork4se.model.Application;
import vn.iwork4se.model.Notification;
import vn.iwork4se.model.User;
import vn.iwork4se.repository.ApplicationRepository;
import vn.iwork4se.repository.NotificationRepository;
import vn.iwork4se.repository.UserRepository;
import vn.iwork4se.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    @Override
    public NotificationCreationResponse save(NotificationCreationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Application application = null;
        if (request.getApplicationId() != null) {
            application = applicationRepository.findById(request.getApplicationId())
                    .orElseThrow(() -> new RuntimeException("Application not found"));
        }

        Notification notification = Notification.builder()
                .id("NOT" + UUID.randomUUID().toString())
                .user(user)
                .application(application)
                .type(request.getType())
                .message(request.getMessage())
                .createdAt(LocalDateTime.now())
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification created successfully, notificationId={}", savedNotification.getId());

        return NotificationCreationResponse.builder()
                .id(savedNotification.getId())
                .userId(savedNotification.getUser().getId())
                .applicationId(savedNotification.getApplication() != null ? savedNotification.getApplication().getId() : null)
                .type(savedNotification.getType())
                .message(savedNotification.getMessage())
                .createdAt(savedNotification.getCreatedAt())
                .build();
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
                                                                       LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> notificationPage = notificationRepository.findByMultipleCriteria(
                userId, type, applicationId, startDate, endDate, pageable);
        
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
                .type(notification.getType())
                .message(notification.getMessage())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
