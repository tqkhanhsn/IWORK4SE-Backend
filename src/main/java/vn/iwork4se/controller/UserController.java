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
import vn.iwork4se.controller.request.ApplicantCreationRequest;
import vn.iwork4se.controller.request.ChangePasswordRequest;
import vn.iwork4se.controller.request.EmployerCreationRequest;
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

    @Operation(summary = "Create a new applicant account", description = "API to create a new applicant in the system")
    @PostMapping("/sign-up")
    public ResponseEntity<Object> createApplicant(@RequestBody @Valid ApplicantCreationRequest request, HttpServletResponse response) throws IOException {
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "Applicant has been successfully created");
        result.put("data", userService.createApplicant(request));
        String redirectUrl = "/swagger-ui/index.html#/Applicant%20controller/updateApplicant";
        result.put("redirectUrl", redirectUrl);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(summary = "Create a new employer account", description = "API to create a new employer in the system")
    @PostMapping("/sign-up/employer")
    public ResponseEntity<Object> createEmployer(@RequestBody @Valid EmployerCreationRequest request, HttpServletResponse response) throws IOException {
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "Employer has been successfully created");
        result.put("data", userService.createEmployer(request));
        String redirectUrl = "/swagger-ui/index.html#/Employer%20controller/updateEmp";
        result.put("redirectUrl", redirectUrl);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(summary = "Change user password", description = "API to change user password")
    @PatchMapping("/change-pwd")
    public Map<String, Object> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        log.info("Changing password for user with request: {}", request);
        userService.changePassword(request);
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

    @Operation(summary = "Request activation", description = "API for user to request account activation")
    @PostMapping("/request-activation")
    public ResponseEntity<Object> requestActivation(@RequestParam String userId) {
        log.info("User {} requesting activation", userId);
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            userService.requestActivation(userId);
            result.put("status", HttpStatus.OK.value());
            result.put("message", "Yêu cầu kích hoạt tài khoản đã được gửi đến admin");
            result.put("data", "");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error requesting activation: {}", e.getMessage());
            result.put("status", HttpStatus.BAD_REQUEST.value());
            result.put("message", e.getMessage());
            result.put("data", "");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Approve activation", description = "API for admin to approve user activation request")
    @PostMapping("/approve-activation")
    public ResponseEntity<Object> approveActivation(@RequestParam String userId) {
        log.info("Admin approving activation for user {}", userId);
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            userService.approveActivation(userId);
            result.put("status", HttpStatus.OK.value());
            result.put("message", "Tài khoản đã được kích hoạt thành công");
            result.put("data", "");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error approving activation: {}", e.getMessage());
            result.put("status", HttpStatus.BAD_REQUEST.value());
            result.put("message", e.getMessage());
            result.put("data", "");
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
