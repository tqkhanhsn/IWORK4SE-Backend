package vn.iwork4se.controller.response;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse implements Serializable {
    private String id;
    private String userId;
    private String userName;
    private String applicationId;
    private String jobPostId;
    private String type;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
}
