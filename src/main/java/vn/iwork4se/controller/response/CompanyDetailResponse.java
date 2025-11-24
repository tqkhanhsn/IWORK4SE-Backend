package vn.iwork4se.controller.response;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDetailResponse implements Serializable {
    private String companyName;
    private String industry;
    private String location;
    private String logoUrl;
    private String description;
    private List<EmployerWithJobsResponse> employers;
    private Integer totalEmployers;
    private Integer totalJobPosts;
}
