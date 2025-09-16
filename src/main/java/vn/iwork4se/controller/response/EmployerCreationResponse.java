package vn.iwork4se.controller.response;

import lombok.*;
import vn.iwork4se.common.Gender;
import vn.iwork4se.common.UserStatus;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployerCreationResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String userName;
//    private String address;
//    private LocalDate birthday;
//    private String phone;
//    private Gender gender;
//    private UserStatus userStatus;
//    private LocalDate createAt;
//    private String message;
//
//    private String companyName;
//    private String location;
//    private String industry;
//    private String description;
//    private String logoUrl;
}
