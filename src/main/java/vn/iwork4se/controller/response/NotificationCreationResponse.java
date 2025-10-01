package vn.iwork4se.controller.response;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCreationResponse implements Serializable {
    private String id;
    private String userId;
    private String applicationId;
    private String type;
    private String message;
    private LocalDateTime createdAt;
}
