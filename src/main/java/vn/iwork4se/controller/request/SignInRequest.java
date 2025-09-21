package vn.iwork4se.controller.request;

import lombok.Getter;

@Getter
public class SignInRequest {
    private String username;
    private String password;
    private String platform;
    private String deviceToken;
    private String versionApp;
}
