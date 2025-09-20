package vn.iwork4se.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
public class UserController {
    private final UserService userService;

    @Operation(summary = "Create a new user", description = "API to create a new user in the system")
    @PostMapping("/create")
    public ResponseEntity<Object> createUser(@RequestBody UserCreationRequest request) {
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "User has been successfully created");
        result.put("data", userService.createUser(request));
        return new ResponseEntity<>(result, HttpStatus.CREATED);

    }

    @Operation(summary = "Confirm email", description = "API to confirm email verification")
    @GetMapping("/confirm-email")
    public void confirmEmail(@RequestParam String secretCode, HttpServletResponse response) throws IOException {
        log.info("Confirming email verification for user with code: {}", secretCode);
        try {

        }catch (Exception e) {
            log.error("Confirm email was failure!, errorMessage+{}",e.getMessage());
        }finally {
            response.sendRedirect("https://www.facebook.com/");
        }

    }
}
