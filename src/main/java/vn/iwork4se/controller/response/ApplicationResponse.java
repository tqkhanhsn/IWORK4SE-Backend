package vn.iwork4se.controller.response;

import co.elastic.clients.util.DateTime;
import lombok.*;
import vn.iwork4se.common.ApplicationStatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

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
    private String logoUrl;
    private String companyName;
    private String location;
    private Double minSalary;
    private Double maxSalary;
    private LocalDate closingDate;
    private LocalDateTime appliedDate;
    private ApplicationStatus status;
    private String cvFileName;
    private String cvId;
    private String cvUrl;
    private LocalDateTime updateAt;
}
