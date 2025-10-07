package vn.iwork4se.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.iwork4se.common.JobStatus;
import vn.iwork4se.common.JobType;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostSearchRequest {
    private String keywords;
    private JobStatus jobStatus;
    private JobType jobType;
    private String location;
    private Double minSalary;
    private Double maxSalary;
    private String experience;
    private Long categoryId;
    private String employerId;
    private LocalDate postedAfter;
    private LocalDate closingBefore;
    private Integer page = 0;
    private Integer size = 20;
}
