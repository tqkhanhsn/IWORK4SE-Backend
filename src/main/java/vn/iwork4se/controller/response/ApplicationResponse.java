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
public class ApplicationResponse implements Serializable {
    private String id;
    private String applicantId;
    private String applicantName;
    private String jobId;
    private String jobTitle;
    private String jobPosition;
    private String companyName;
    private String cvId;
    private String cvUrl;
    private LocalDateTime appliedAt;
    private ApplicationStatus applicationStatus;
    private LocalDateTime updateAt;
}
