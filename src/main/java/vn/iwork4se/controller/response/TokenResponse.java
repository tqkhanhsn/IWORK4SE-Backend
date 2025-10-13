package vn.iwork4se.controller.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.iwork4se.model.Role;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class TokenResponse implements Serializable {
    private String accessToken;
    private String refreshToken;
    private String role;
    private String userId;
    private String fullName;
    private String email;
    private String phone;
}
