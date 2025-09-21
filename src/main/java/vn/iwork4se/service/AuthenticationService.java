package vn.iwork4se.service;

import vn.iwork4se.controller.request.SignInRequest;
import vn.iwork4se.controller.response.TokenResponse;

public interface AuthenticationService {
    TokenResponse getAccessToken(SignInRequest request);
    TokenResponse getRefreshToken(String refreshToken);
}
