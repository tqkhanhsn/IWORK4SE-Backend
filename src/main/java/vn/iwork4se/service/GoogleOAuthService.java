package vn.iwork4se.service;

import vn.iwork4se.controller.response.TokenResponse;

public interface GoogleOAuthService {
    TokenResponse loginWithGoogle(String idToken, String platform, String deviceToken, String versionApp);
}
