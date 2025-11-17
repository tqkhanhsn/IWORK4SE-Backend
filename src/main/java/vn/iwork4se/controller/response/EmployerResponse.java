package vn.iwork4se.controller.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import vn.iwork4se.common.Gender;
import vn.iwork4se.common.UserStatus;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployerResponse implements Serializable {
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
    private String companyName;
    private String location;
    private String industry;
    private String description;
    private String logoUrl;
}
