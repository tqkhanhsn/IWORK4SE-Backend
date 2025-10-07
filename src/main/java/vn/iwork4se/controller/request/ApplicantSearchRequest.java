package vn.iwork4se.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.iwork4se.common.Gender;
import vn.iwork4se.common.UserStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantSearchRequest {
    private String keywords;
    private Integer minExperience;
    private Double minGpa;
    private String skill;
    private String major;
    private String university;
    private Gender gender;
    private UserStatus userStatus;
    private Integer page = 0;
    private Integer size = 20;
}
