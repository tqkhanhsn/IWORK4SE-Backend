package vn.iwork4se.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import vn.iwork4se.event.ApplicationStatusChangedEvent;
import vn.iwork4se.model.Application;
import vn.iwork4se.model.Applicant;
import vn.iwork4se.model.Employer;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationStatusListener {
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void onApplicationStatusChanged(ApplicationStatusChangedEvent event) {
        Application application = event.getApplication();
        String newStatus = event.getNewStatus().toString();
        String oldStatus = event.getOldStatus().toString();

        log.info("[EVENT] Application status change event received - ID: {}, Old Status: {}, New Status: {}",
                application.getId(), oldStatus, newStatus);

        // Get applicant and employer information
        Applicant applicant = (Applicant) application.getApplicant();
        Employer employer = (Employer) application.getJob().getEmployer();

        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "APPLICATION_STATUS_CHANGED");
        notification.put("applicationId", application.getId());
        notification.put("jobTitle", application.getJob().getTitle());
        notification.put("jobPosition", application.getJob().getJobPosition());
        notification.put("companyName", employer.getCompanyName());
        notification.put("applicantName", applicant.getFirstName() + " " + applicant.getLastName());
        notification.put("oldStatus", oldStatus);
        notification.put("newStatus", newStatus);
        notification.put("message", getStatusChangeMessage(newStatus));
        notification.put("changedAt", event.getChangedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        notification.put("timestamp", System.currentTimeMillis());

        log.debug("[SOCKET] Broadcasting application status change to applicant topic: /topic/notifications/applicant/{}",
                applicant.getId());
        messagingTemplate.convertAndSend(
                "/topic/notifications/applicant/" + applicant.getId(),
                notification
        );
        log.info("[SOCKET] Notification sent to applicant: {}", applicant.getId());

        log.debug("[SOCKET] Broadcasting application status change to employer topic: /topic/notifications/employer/{}",
                employer.getId());
        messagingTemplate.convertAndSend(
                "/topic/notifications/employer/" + employer.getId(),
                notification
        );
        log.info("[SOCKET] Notification sent to employer: {}", employer.getId());

        log.info("[EVENT] Application {} status changed from {} to {}. Notifications sent to applicant: {} and employer: {}",
                application.getId(), oldStatus, newStatus, applicant.getId(), employer.getId());
    }

    private String getStatusChangeMessage(String status) {
        return switch (status) {
            case "APPROVED" -> "Congratulations! Your application has been approved!";
            case "REJECTED" -> "Unfortunately, your application has been rejected. Better luck next time!";
            case "WITHDRAWN" -> "Your application has been withdrawn.";
            case "PENDING" -> "Your application is pending review.";
            default -> "Your application status has been updated.";
        };
    }
}
