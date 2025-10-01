package vn.iwork4se.controller.response;

import lombok.*;
import vn.iwork4se.common.ApplicationStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationCreationResponse implements Serializable {
    private String id;
    private String applicantId;
    private String jobId;
    private String cvId;
    private LocalDateTime appliedAt;
    private ApplicationStatus applicationStatus;
}
