package vn.iwork4se.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import vn.iwork4se.common.Gender;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class ApplicantCreationRequest {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Username is required")
    private String userName;

    @NotBlank(message = "Password is required")
    private String password;

//    private String address;
//    private LocalDate birthday;
//    private String phone;
//    private Gender gender;
//
//    private Integer yearsOfExperience;
//    private String careerObjective;
//    private String universityName;
//    private Double gpa;
//    private String major;
}
