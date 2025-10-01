package vn.iwork4se.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.iwork4se.controller.request.ChangePasswordRequest;
import vn.iwork4se.controller.request.EmployerUpdateRequest;
import vn.iwork4se.controller.response.EmployerResponse;
import vn.iwork4se.service.EmployerService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/employer")
@Tag(name="Employer controller")
@Slf4j(topic = "EmployerController")
@RequiredArgsConstructor
@Validated
public class EmployerController {
    private final EmployerService employerService;


    @Operation(summary = "Update employer", description = "API to update employer in the system")
    @PutMapping("/update")
    public Map<String, Object> updateEmp(@RequestBody @Valid EmployerUpdateRequest request) {
        log.info("Updating employer with request: {}", request);
        employerService.updateEmployer(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.ACCEPTED.value());
        result.put("message", "User has been successfully updated");
        result.put("data", "");
        return result;
    }


    @Operation(summary = "Get employer detail", description = "API to get user detail by ID")
    @GetMapping("/{id}")
    public Map<String, Object> getUserDetail(@PathVariable String id) {
        log.info("Getting employer detail for user ID: {}", id);
        EmployerResponse empDetail =  employerService.findEmployerById(id);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "User detail has been successfully retrieved");
        result.put("data", empDetail);
        return result;
    }

    @Operation(summary = "get list of employers", description = "API to get list of employers")
    @GetMapping("/list")
    public Map<String, Object> getApplicant(@RequestParam (required = false) String keyword,
                                            @RequestParam (required = false) String sort,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        log.info("Getting list of users");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "List of users has been successfully retrieved");
        result.put("data", employerService.findAllEmployers(keyword, sort, page, size));
        return result;
    }
    @Operation(summary = "Delete employer", description = "API to delete a employer by ID")
    @DeleteMapping("/del/{id}")
    public Map<String, Object> deleteEmployer(@PathVariable String id) {
        log.info("Deleting user with ID: {}", id);
        employerService.deleteEmployerById(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.RESET_CONTENT.value());
        result.put("message", "Employer has been successfully deleted");
        result.put("data", "");
        return result;
    }


}
