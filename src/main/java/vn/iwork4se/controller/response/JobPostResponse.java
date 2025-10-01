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
public class JobPostResponse implements Serializable {
    private String id;
    private String title;
    private String description;
    private String jobPosition;
    private String location;
    private String experience;
    private Double minSalary;
    private Double maxSalary;
    private LocalDate postedDate;
    private LocalDate closingDate;
    private Integer vacancies;
    private JobStatus jobStatus;
    private JobType jobType;
    private LocalDateTime updateAt;
    private String employerId;
    private String employerName;
    private String categoryId;
    private String categoryName;
}
