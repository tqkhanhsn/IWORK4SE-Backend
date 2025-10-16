package vn.iwork4se.controller.response;

import lombok.*;
import vn.iwork4se.common.JobStatus;
import vn.iwork4se.common.JobType;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedJobResponse implements Serializable {
    private String id;
    private String applicantId;
    private String applicantName;
    private String jobId;
    private String jobTitle;
    private String jobPosition;
    private String jobLocation;
    private String companyName;
    private LocalDate savedDate;

    private String experience;
    private Double minSalary;
    private Double maxSalary;
    private LocalDate postedDate;
    private LocalDate closingDate;
    private Integer vacancies;
    private JobType jobType;
    private String logoUrl;

}
