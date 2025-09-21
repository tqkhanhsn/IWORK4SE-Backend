package vn.iwork4se.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.iwork4se.controller.request.SignInRequest;
import vn.iwork4se.controller.response.TokenResponse;
import vn.iwork4se.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
@Slf4j(topic = "AUTHENTICATION_CONTROLLER")
@Tag(name = "Authentication controller")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Access token", description = "API to get access token")
    @PostMapping("/access-token")
    public TokenResponse getAccessToken(@RequestBody SignInRequest request) {
        log.info("Getting access token");
        return authenticationService.getAccessToken(request);
    }

    @Operation(summary = "Refresh token", description = "API to get refresh token")
    @PostMapping("/refresh-token")
    public TokenResponse getRefreshToken(@RequestBody String refreshToken) {
        log.info("Getting refresh token");
        return authenticationService.getRefreshToken(refreshToken);
    }
}
