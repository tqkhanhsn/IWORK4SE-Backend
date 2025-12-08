package vn.iwork4se.controller.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class GoogleLoginRequest implements Serializable {
    private String credential;
    private String platform;
    private String deviceToken;
    private String versionApp;
}
