package vn.iwork4se.controller.response;

import lombok.*;
import vn.iwork4se.common.UserType;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationResponse implements Serializable {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String userName;
    private UserType userType;

    private String accessToken;
    private String refreshToken;

}
