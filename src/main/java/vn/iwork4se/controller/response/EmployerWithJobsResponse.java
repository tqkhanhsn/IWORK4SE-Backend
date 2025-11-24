package vn.iwork4se.controller.response;

import lombok.*;
import vn.iwork4se.common.Gender;
import vn.iwork4se.common.UserStatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployerWithJobsResponse implements Serializable {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String companyName;
    private String location;
    private String industry;
    private String description;
    private String logoUrl;
    private UserStatus userStatus;
    private List<JobPostResponse> jobPosts;
}
