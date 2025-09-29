package vn.iwork4se.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.iwork4se.common.UserType;
import vn.iwork4se.controller.request.ChangePasswordRequest;
import vn.iwork4se.controller.request.UserCreationRequest;
import vn.iwork4se.service.UserService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Tag(name="User controller")
@Slf4j(topic = "UserController")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @Operation(summary = "Create a new user", description = "API to create a new user in the system")
    @PostMapping("/sign-up")
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserCreationRequest request, HttpServletResponse response) throws IOException {
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "User has been successfully created");
        result.put("data", userService.createUser(request));
        String redirectUrl = null;
        if(request.getUserType() == UserType.EMPLOYER){
            redirectUrl = "/swagger-ui/index.html#/Employer%20controller/updateEmp";
        } else if(request.getUserType() == UserType.APPLICANT){
            redirectUrl = "/swagger-ui/index.html#/Applicant%20controller/updateApplicant";
        }

        result.put("redirectUrl", redirectUrl);

        return new ResponseEntity<>(result, HttpStatus.CREATED);

    }

    @Operation(summary = "Change user password", description = "API to change user password")
    @PatchMapping("/change-pwd")
    public Map<String, Object> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        log.info("Changing password for user with request: {}", request);
        userService.changePasswordEmployer(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.NO_CONTENT.value());
        result.put("message", "User password has been successfully changed");
        result.put("data", "");
        return result;
    }


    @Operation(summary = "Confirm email", description = "API to confirm email verification")
    @GetMapping("/confirm-email")
    public void confirmEmail(@RequestParam String email, @RequestParam String secretCode, HttpServletResponse response) throws IOException {
        log.info("Confirming email verification for user with code: {}", secretCode);
        try {
            boolean valid = userService.verifySecretCode(email, secretCode);
            if(valid){
                response.sendRedirect("https://www.facebook.com/");
            } else {
                response.sendRedirect("https://www.facebook.com/error");
            }

        }catch (Exception e) {
            log.error("Confirm email was failure!, errorMessage+{}",e.getMessage());
            response.sendRedirect("https://www.facebook.com/error");
        }
    }
}
