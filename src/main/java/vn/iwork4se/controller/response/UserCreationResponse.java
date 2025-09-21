package vn.iwork4se.controller.response;

import lombok.*;

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

}
