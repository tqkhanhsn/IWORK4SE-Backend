package vn.iwork4se.controller.response;

import lombok.*;
import vn.iwork4se.common.JobType;

import java.io.Serializable;
import java.time.LocalDate;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostCreationResponse implements Serializable {
    private String id;
    private String title;
    private String description;
    private String location;
    private String experience;
    private String jobPosition;
    private JobType jobType;
    private LocalDate closingDate;
    private Double minSalary;
    private String employerId;

}
