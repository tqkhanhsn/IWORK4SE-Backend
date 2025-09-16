package vn.iwork4se.controller.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import vn.iwork4se.common.Gender;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployerResponse implements Serializable {
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private LocalDate birthday;
    private String phone;
    private Gender gender;
    private String companyName;
    private String location;
    private String industry;
    private String description;
    private String logoUrl;
}
