package vn.iwork4se.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.iwork4se.common.ApplicationStatus;
import vn.iwork4se.controller.request.ApplicationCreationRequest;
import vn.iwork4se.controller.request.ApplicationUpdateRequest;
import vn.iwork4se.service.ApplicationService;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/application")
@Validated
@Slf4j
@Tag(name = "Application Controller")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApplicationController {
    private final ApplicationService applicationService;

    @Operation(method = "POST", summary = "Create new application", description = "Submit a job application")
    @PostMapping(value = "/")
    public ResponseEntity<Object> createApplication(@Valid @RequestBody ApplicationCreationRequest request) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "Application submitted successfully");
        result.put("data", applicationService.save(request));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(method = "PUT", summary = "Update application", description = "Update an existing application")
    @PutMapping(value = "/update")
    public ResponseEntity<Object> updateApplication(@Valid @RequestBody ApplicationUpdateRequest request) {
        applicationService.updateApplication(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Application updated successfully");
        result.put("data", "");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get application by ID", description = "Retrieve a specific application by its ID")
    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getApplicationById(@PathVariable String id) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Application retrieved successfully");
        result.put("data", applicationService.findApplicationById(id));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get applications by applicant", description = "Retrieve all applications for a specific applicant")
    @GetMapping(value = "/applicant/{applicantId}")
    public ResponseEntity<Object> getApplicationsByApplicant(
            @PathVariable String applicantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Applications retrieved successfully");
        result.put("data", applicationService.findApplicationsByApplicant(applicantId, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get applications by job", description = "Retrieve all applications for a specific job")
    @GetMapping(value = "/job/{jobId}")
    public ResponseEntity<Object> getApplicationsByJob(
            @PathVariable String jobId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Applications retrieved successfully");
        result.put("data", applicationService.findApplicationsByJob(jobId, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get applications by employer", description = "Retrieve all applications for jobs posted by an employer")
    @GetMapping(value = "/employer/{employerId}")
    public ResponseEntity<Object> getApplicationsByEmployer(
            @PathVariable String employerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {

        ApplicationStatus applicationStatus = null;
        if (status != null && !status.trim().isEmpty()) {
            applicationStatus = ApplicationStatus.valueOf(status.toUpperCase());
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Applications retrieved successfully");
        result.put("data", applicationService.findApplicationsByEmployer(employerId, applicationStatus, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get applications by status", description = "Retrieve applications by status")
    @GetMapping(value = "/status/{status}")
    public ResponseEntity<Object> getApplicationsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        ApplicationStatus applicationStatus = ApplicationStatus.valueOf(status.toUpperCase());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Applications retrieved successfully");
        result.put("data", applicationService.findApplicationsByStatus(applicationStatus, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get applications by applicant and status", description = "Retrieve applications by applicant and status")
    @GetMapping(value = "/applicant/{applicantId}/status/{status}")
    public ResponseEntity<Object> getApplicationsByApplicantAndStatus(
            @PathVariable String applicantId,
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        ApplicationStatus applicationStatus = ApplicationStatus.valueOf(status.toUpperCase());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Applications retrieved successfully");
        result.put("data", applicationService.findApplicationsByApplicantAndStatus(applicantId, applicationStatus, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get applications by job and status", description = "Retrieve applications by job and status")
    @GetMapping(value = "/job/{jobId}/status/{status}")
    public ResponseEntity<Object> getApplicationsByJobAndStatus(
            @PathVariable String jobId,
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        ApplicationStatus applicationStatus = ApplicationStatus.valueOf(status.toUpperCase());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Applications retrieved successfully");
        result.put("data", applicationService.findApplicationsByJobAndStatus(jobId, applicationStatus, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get recent applications", description = "Retrieve recent applications (last 30 days)")
    @GetMapping(value = "/recent")
    public ResponseEntity<Object> getRecentApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Recent applications retrieved successfully");
        result.put("data", applicationService.findRecentApplications(page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get applications by date range", description = "Retrieve applications within a date range")
    @GetMapping(value = "/date-range")
    public ResponseEntity<Object> getApplicationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Applications retrieved successfully");
        result.put("data", applicationService.findApplicationsByDateRange(startDate, endDate, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Search applications with multiple criteria", description = "Search applications with multiple filtering criteria")
    @GetMapping(value = "/search")
    public ResponseEntity<Object> searchApplications(
            @RequestParam(required = false) String applicantId,
            @RequestParam(required = false) String jobId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        ApplicationStatus applicationStatus = null;
        if (status != null && !status.trim().isEmpty()) {
            applicationStatus = ApplicationStatus.valueOf(status.toUpperCase());
        }
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Applications retrieved successfully");
        result.put("data", applicationService.findApplicationsByMultipleCriteria(
                applicantId, jobId, applicationStatus, startDate, endDate, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "PUT", summary = "Update application status", description = "Update the status of an application")
    @PutMapping(value = "/{id}/status")
    public ResponseEntity<Object> updateApplicationStatus(
            @PathVariable String id,
            @RequestParam String status) {
        
        ApplicationStatus applicationStatus = ApplicationStatus.valueOf(status.toUpperCase());
        applicationService.updateApplicationStatus(id, applicationStatus);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Application status updated successfully");
        result.put("data", "");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "PUT", summary = "Approve application", description = "Approve an application")
    @PutMapping(value = "/{id}/approve")
    public ResponseEntity<Object> approveApplication(@PathVariable String id) {
        applicationService.approveApplication(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Application approved successfully");
        result.put("data", "");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "PUT", summary = "Reject application", description = "Reject an application")
    @PutMapping(value = "/{id}/reject")
    public ResponseEntity<Object> rejectApplication(@PathVariable String id) {
        applicationService.rejectApplication(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Application rejected successfully");
        result.put("data", "");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "PUT", summary = "Withdraw application", description = "Withdraw an application")
    @PutMapping(value = "/{id}/withdraw")
    public ResponseEntity<Object> withdrawApplication(@PathVariable String id) {
        applicationService.withdrawApplication(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Application withdrawn successfully");
        result.put("data", "");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Check if applicant applied", description = "Check if an applicant has applied for a specific job")
    @GetMapping(value = "/check")
    public ResponseEntity<Object> hasApplicantApplied(
            @RequestParam String applicantId,
            @RequestParam String jobId) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Check completed");
        result.put("data", applicationService.hasApplicantAppliedForJob(applicantId, jobId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Count applications by applicant", description = "Get count of applications for an applicant")
    @GetMapping(value = "/applicant/{applicantId}/count")
    public ResponseEntity<Object> countApplicationsByApplicant(@PathVariable String applicantId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Applications count retrieved successfully");
        result.put("data", applicationService.countApplicationsByApplicant(applicantId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Count applications by job", description = "Get count of applications for a job")
    @GetMapping(value = "/job/{jobId}/count")
    public ResponseEntity<Object> countApplicationsByJob(@PathVariable String jobId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Applications count retrieved successfully");
        result.put("data", applicationService.countApplicationsByJob(jobId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Count applications by status", description = "Get count of applications by status")
    @GetMapping(value = "/status/{status}/count")
    public ResponseEntity<Object> countApplicationsByStatus(@PathVariable String status) {
        ApplicationStatus applicationStatus = ApplicationStatus.valueOf(status.toUpperCase());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Applications count retrieved successfully");
        result.put("data", applicationService.countApplicationsByStatus(applicationStatus));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "DELETE", summary = "Delete application", description = "Delete an application by its ID")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> deleteApplication(@PathVariable String id) {
        applicationService.deleteApplication(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Application deleted successfully");
        result.put("data", "");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
