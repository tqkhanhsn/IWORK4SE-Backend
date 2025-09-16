package vn.iwork4se.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import vn.iwork4se.common.Gender;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@ToString
public class EmployerCreationRequest implements Serializable {
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
//    @NotBlank(message = "Company name is required")
//    private String companyName;
//
//    private String location;
//    private String industry;
//    private String description;
//    private String logoUrl;
}
