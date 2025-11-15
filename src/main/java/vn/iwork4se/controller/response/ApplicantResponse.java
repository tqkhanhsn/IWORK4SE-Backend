package vn.iwork4se.controller.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import vn.iwork4se.common.DegreeLevel;
import vn.iwork4se.common.Gender;
import vn.iwork4se.common.UserStatus;
import vn.iwork4se.controller.request.CertificateRequest;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantResponse implements Serializable {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String userName;
    private String address;
    private LocalDate birthday;
    private String phone;
    private Gender gender;
    private UserStatus userStatus;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Integer yearsOfExperience;
    private String careerObjective;
    private String universityName;
    private DegreeLevel degreeLevel;
    private Integer graduationYear;
    private Double gpa;
    private String major;
    private List<CertificateResponse> certificates;
    private List<String> Skills;
}
