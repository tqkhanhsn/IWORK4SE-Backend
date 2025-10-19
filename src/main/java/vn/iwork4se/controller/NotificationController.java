package vn.iwork4se.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.iwork4se.controller.request.NotificationCreationRequest;
import vn.iwork4se.service.NotificationService;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/notification")
@Validated
@Slf4j
@Tag(name = "Notification Controller")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(method = "POST", summary = "Create notification", description = "Create a new notification")
    @PostMapping(value = "/")
    public ResponseEntity<Object> createNotification(@Valid @RequestBody NotificationCreationRequest request) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "Notification created successfully");
        result.put("data", notificationService.save(request));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(method = "POST", summary = "Create application status notification", description = "Create a notification for application status change")
    @PostMapping(value = "/application-status")
    public ResponseEntity<Object> createApplicationStatusNotification(
            @RequestParam String userId,
            @RequestParam String applicationId,
            @RequestParam String message) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "Application status notification created successfully");
        result.put("data", notificationService.createApplicationStatusNotification(userId, applicationId, message));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(method = "POST", summary = "Create job match notification", description = "Create a notification for job match")
    @PostMapping(value = "/job-match")
    public ResponseEntity<Object> createJobMatchNotification(
            @RequestParam String userId,
            @RequestParam String message) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "Job match notification created successfully");
        result.put("data", notificationService.createJobMatchNotification(userId, message));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(method = "POST", summary = "Create system notification", description = "Create a system notification")
    @PostMapping(value = "/system")
    public ResponseEntity<Object> createSystemNotification(
            @RequestParam String userId,
            @RequestParam String message) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "System notification created successfully");
        result.put("data", notificationService.createSystemNotification(userId, message));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(method = "GET", summary = "Get notification by ID", description = "Retrieve a specific notification by its ID")
    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getNotificationById(@PathVariable String id) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Notification retrieved successfully");
        result.put("data", notificationService.findNotificationById(id));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get notifications by user", description = "Retrieve all notifications for a specific user")
    @GetMapping(value = "/user/{userId}")
    public ResponseEntity<Object> getNotificationsByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Notifications retrieved successfully");
        result.put("data", notificationService.findNotificationsByUserOrderByDate(userId, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get recent notifications by user", description = "Retrieve recent notifications (last 7 days) for a user")
    @GetMapping(value = "/user/{userId}/recent")
    public ResponseEntity<Object> getRecentNotificationsByUser(@PathVariable String userId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Recent notifications retrieved successfully");
        result.put("data", notificationService.findRecentNotificationsByUser(userId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get notifications by type", description = "Retrieve notifications by type")
    @GetMapping(value = "/type/{type}")
    public ResponseEntity<Object> getNotificationsByType(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Notifications retrieved successfully");
        result.put("data", notificationService.findNotificationsByType(type, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get notifications by user and type", description = "Retrieve notifications by user and type")
    @GetMapping(value = "/user/{userId}/type/{type}")
    public ResponseEntity<Object> getNotificationsByUserAndType(
            @PathVariable String userId,
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Notifications retrieved successfully");
        result.put("data", notificationService.findNotificationsByUserAndType(userId, type, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get notifications by application", description = "Retrieve notifications related to a specific application")
    @GetMapping(value = "/application/{applicationId}")
    public ResponseEntity<Object> getNotificationsByApplication(
            @PathVariable String applicationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Notifications retrieved successfully");
        result.put("data", notificationService.findNotificationsByApplication(applicationId, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get notifications by date range", description = "Retrieve notifications within a date range")
    @GetMapping(value = "/date-range")
    public ResponseEntity<Object> getNotificationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Notifications retrieved successfully");
        result.put("data", notificationService.findNotificationsByDateRange(startDate, endDate, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get notifications by user and date range", description = "Retrieve notifications by user within a date range")
    @GetMapping(value = "/user/{userId}/date-range")
    public ResponseEntity<Object> getNotificationsByUserAndDateRange(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Notifications retrieved successfully");
        result.put("data", notificationService.findNotificationsByUserAndDateRange(userId, startDate, endDate, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Search notifications with multiple criteria", description = "Search notifications with multiple filtering criteria")
    @GetMapping(value = "/search")
    public ResponseEntity<Object> searchNotifications(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String applicationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Notifications retrieved successfully");
        result.put("data", notificationService.findNotificationsByMultipleCriteria(
                userId, type, applicationId, startDate, endDate, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Count notifications by user", description = "Get count of notifications for a user")
    @GetMapping(value = "/user/{userId}/count")
    public ResponseEntity<Object> countNotificationsByUser(@PathVariable String userId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Notifications count retrieved successfully");
        result.put("data", notificationService.countNotificationsByUser(userId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Count notifications by type", description = "Get count of notifications by type")
    @GetMapping(value = "/type/{type}/count")
    public ResponseEntity<Object> countNotificationsByType(@PathVariable String type) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Notifications count retrieved successfully");
        result.put("data", notificationService.countNotificationsByType(type));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "DELETE", summary = "Delete notification", description = "Delete a notification by its ID")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> deleteNotification(@PathVariable String id) {
        notificationService.deleteNotification(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Notification deleted successfully");
        result.put("data", "");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "DELETE", summary = "Delete notifications by user", description = "Delete all notifications for a user")
    @DeleteMapping(value = "/user/{userId}")
    public ResponseEntity<Object> deleteNotificationsByUser(@PathVariable String userId) {
        notificationService.deleteNotificationsByUser(userId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "All notifications deleted for user");
        result.put("data", "");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "DELETE", summary = "Delete old notifications", description = "Delete notifications older than specified date")
    @DeleteMapping(value = "/cleanup")
    public ResponseEntity<Object> deleteOldNotifications(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beforeDate) {
        
        notificationService.deleteOldNotifications(beforeDate);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Old notifications deleted successfully");
        result.put("data", "");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "PUT", summary = "Mark notification as read", description = "Mark a specific notification as read")
    @PutMapping(value = "/{id}/mark-read")
    public ResponseEntity<Object> markNotificationAsRead(@PathVariable String id) {
        notificationService.markNotificationAsRead(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Notification marked as read successfully");
        result.put("data", "");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "PUT", summary = "Mark all notifications as read", description = "Mark all notifications for a user as read")
    @PutMapping(value = "/user/{userId}/mark-all-read")
    public ResponseEntity<Object> markAllNotificationsAsRead(@PathVariable String userId) {
        notificationService.markAllNotificationsAsRead(userId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "All notifications marked as read successfully");
        result.put("data", "");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get unread notification count", description = "Get count of unread notifications for a user")
    @GetMapping(value = "/user/{userId}/unread-count")
    public ResponseEntity<Object> getUnreadNotificationCount(@PathVariable String userId) {
        long count = notificationService.countUnreadNotificationsByUser(userId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Unread notification count retrieved successfully");
        result.put("data", count);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
