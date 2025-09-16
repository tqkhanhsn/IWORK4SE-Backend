package vn.iwork4se.controller.response;

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
public class EmployerCreationResponse implements Serializable {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String userName;
}
