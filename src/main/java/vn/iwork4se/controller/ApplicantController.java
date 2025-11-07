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
import vn.iwork4se.controller.request.ApplicantUpdateRequest;
import vn.iwork4se.controller.request.ChangePasswordRequest;
import vn.iwork4se.controller.response.ApplicantResponse;
import vn.iwork4se.service.ApplicantService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/applicant")
@Tag(name="Applicant controller")
@Slf4j(topic = "ApplicantController")
@RequiredArgsConstructor
@Validated
public class ApplicantController {
    private final ApplicantService applicantService;
    @Operation(summary = "Update an applicant", description = "API to update an existing applicant in the system")
    @PutMapping("/update")
    public Map<String, Object> updateApplicant(@RequestBody @Valid ApplicantUpdateRequest request) {
        log.info("Updating applicant with request: {}", request);
        applicantService.updateApplicant(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.ACCEPTED.value());
        result.put("message", "User has been successfully updated");
        result.put("data", "");
        return result;
    }


    @Operation(summary = "Get applicant detail", description = "API to get user detail by ID")
    @GetMapping("/{id}")
    public Map<String, Object> getUserDetail(@PathVariable String id) {
        log.info("Getting employer detail for user ID: {}", id);
        ApplicantResponse appDetail =  applicantService.findApplicantById(id);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "User detail has been successfully retrieved");
        result.put("data", appDetail);
        return result;
    }

    @Operation(summary = "get list of applicants", description = "API to get list of applicants")
    @GetMapping("/list")
    public Map<String, Object> getApplicant(@RequestParam (required = false) String keyword,
                                        @RequestParam (required = false) String sort,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size) {
        log.info("Getting list of users");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "List of users has been successfully retrieved");
        result.put("data", applicantService.findAllApplicants(keyword, sort, page, size));
        return result;
    }

    @Operation(summary = "Delete applicant", description = "API to delete a applicant by ID")
    @DeleteMapping("/del/{id}")
    public Map<String, Object> deleteApplicant(@PathVariable String id) {
        log.info("Deleting user with ID: {}", id);
        applicantService.deleteApplicantById(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.RESET_CONTENT.value());
        result.put("message", "Applicant has been successfully deleted");
        result.put("data", "");
        return result;
    }



}


