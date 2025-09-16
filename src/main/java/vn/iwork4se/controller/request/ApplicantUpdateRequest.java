package vn.iwork4se.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import vn.iwork4se.common.Gender;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
public class ApplicantUpdateRequest implements Serializable {
    @NotNull
    private String id;
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;
    private String address;
    private LocalDate birthday;
    private String phone;
    private Gender gender;
    private Integer yearsOfExperience;
    private String careerObjective;
    private String universityName;
    private Double gpa;
    private String major;
    private List<CertificateRequest> certificates;
    private List<String> Skills;
}
