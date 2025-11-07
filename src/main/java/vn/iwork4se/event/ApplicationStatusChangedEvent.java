package vn.iwork4se.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import vn.iwork4se.common.ApplicationStatus;
import vn.iwork4se.model.Application;

import java.time.LocalDateTime;

@Getter
public class ApplicationStatusChangedEvent extends ApplicationEvent {
    private final Application application;
    private final ApplicationStatus newStatus;
    private final ApplicationStatus oldStatus;
    private final LocalDateTime changedAt;

    public ApplicationStatusChangedEvent(Object source, Application application,
                                         ApplicationStatus newStatus, ApplicationStatus oldStatus) {
        super(source);
        this.application = application;
        this.newStatus = newStatus;
        this.oldStatus = oldStatus;
        this.changedAt = LocalDateTime.now();
    }
}
