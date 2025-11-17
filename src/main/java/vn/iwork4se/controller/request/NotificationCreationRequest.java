package vn.iwork4se.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class NotificationCreationRequest implements Serializable {
    @NotBlank(message = "user ID must not be blank")
    private String userId;

    private String applicationId; // Optional - related application

    private String jobPostId; // Optional - related job post

    @NotBlank(message = "notification type must not be blank")
    private String type;

    @NotBlank(message = "message must not be blank")
    @Size(min = 1, max = 1000, message = "message length must be between 1 and 1000 characters")
    private String message;
}
