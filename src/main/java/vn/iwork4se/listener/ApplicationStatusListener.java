package vn.iwork4se.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import vn.iwork4se.event.ApplicationStatusChangedEvent;
import vn.iwork4se.model.Application;
import vn.iwork4se.model.Applicant;
import vn.iwork4se.model.Employer;
import vn.iwork4se.service.NotificationService;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationStatusListener {
    private final NotificationService notificationService;

    @EventListener
    public void onApplicationStatusChanged(ApplicationStatusChangedEvent event) {
        Application application = event.getApplication();
        String newStatus = event.getNewStatus().toString();
        String oldStatus = event.getOldStatus().toString();

        log.info("[EVENT] Application status change event received - ID: {}, Old Status: {}, New Status: {}",
                application.getId(), oldStatus, newStatus);

        Applicant applicant = application.getApplicant();
        Employer employer = application.getJob().getEmployer();

        String jobTitle = application.getJob().getTitle();
        String companyName = employer.getCompanyName();
        String applicantName = applicant.getFirstName() + " " + applicant.getLastName();
        String statusLabel = getStatusLabel(newStatus);

        String applicantMessage = String.format(
                "Đơn ứng tuyển vị trí %s tại %s của bạn đã chuyển sang trạng thái %s.",
                jobTitle, companyName, statusLabel);
        notificationService.createApplicationStatusNotification(
                applicant.getId(),
                application.getId(),
                applicantMessage
        );

        String employerMessage = String.format(
                "Ứng viên %s cho vị trí %s hiện đang ở trạng thái %s.",
                applicantName, jobTitle, statusLabel);
        notificationService.createApplicationStatusNotification(
                employer.getId(),
                application.getId(),
                employerMessage
        );

        log.info("[EVENT] Application {} status changed from {} to {}. Notifications persisted for applicant {} and employer {}",
                application.getId(), oldStatus, newStatus, applicant.getId(), employer.getId());
    }

    private String getStatusLabel(String status) {
        return switch (status) {
            case "APPROVED" -> "Được chấp nhận";
            case "REJECTED" -> "Bị từ chối";
            case "WITHDRAWN" -> "Đã rút";
            case "PENDING" -> "Đang chờ duyệt";
            default -> status;
        };
    }
}
