package vn.iwork4se.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iwork4se.controller.request.ApplicantCreationRequest;
import vn.iwork4se.controller.request.EmployerCreationRequest;
import vn.iwork4se.controller.request.EmployerUpdateRequest;
import vn.iwork4se.service.ApplicantService;
import vn.iwork4se.service.EmployerService;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/employer")
@Tag(name="Employer controller")
@Slf4j(topic = "EmployerController")
@RequiredArgsConstructor
//@Validated
public class EmployerController {
    private final EmployerService employerService;

    @Operation(summary = "Create a new employer", description = "API to create a new employer in the system")
    @PostMapping("/create")
    public ResponseEntity<Object> createUser(@RequestBody EmployerCreationRequest request) {
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "User has been successfully created");
        result.put("data", employerService.save(request));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(summary = "Update employer", description = "API to update employer in the system")
    @PutMapping("/update")
    public Map<String, Object> updateEmp(@RequestBody EmployerUpdateRequest request) {
        log.info("Updating employer with request: {}", request);
        employerService.updateEmployer(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.ACCEPTED.value());
        result.put("message", "User has been successfully updated");
        result.put("data", "");
        return result;
    }
}
