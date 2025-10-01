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
import vn.iwork4se.controller.request.SavedJobCreationRequest;
import vn.iwork4se.service.SavedJobService;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/saved-job")
@Validated
@Slf4j
@Tag(name = "Saved Job Controller")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class SavedJobController {
    private final SavedJobService savedJobService;

    @Operation(method = "POST", summary = "Save a job", description = "Save a job for an applicant")
    @PostMapping(value = "/")
    public ResponseEntity<Object> saveJob(@Valid @RequestBody SavedJobCreationRequest request) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "Job saved successfully");
        result.put("data", savedJobService.save(request));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(method = "POST", summary = "Toggle save job", description = "Save or unsave a job for an applicant")
    @PostMapping(value = "/toggle")
    public ResponseEntity<Object> toggleSaveJob(
            @RequestParam String applicantId,
            @RequestParam String jobId) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        Object response = savedJobService.toggleSaveJob(applicantId, jobId);
        
        if (response == null) {
            result.put("status", HttpStatus.OK.value());
            result.put("message", "Job unsaved successfully");
            result.put("data", "");
        } else {
            result.put("status", HttpStatus.CREATED.value());
            result.put("message", "Job saved successfully");
            result.put("data", response);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get saved job by ID", description = "Retrieve a specific saved job by its ID")
    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getSavedJobById(@PathVariable String id) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Saved job retrieved successfully");
        result.put("data", savedJobService.findSavedJobById(id));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get saved jobs by applicant", description = "Retrieve all saved jobs for a specific applicant")
    @GetMapping(value = "/applicant/{applicantId}")
    public ResponseEntity<Object> getSavedJobsByApplicant(
            @PathVariable String applicantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Saved jobs retrieved successfully");
        result.put("data", savedJobService.findSavedJobsByApplicant(applicantId, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get saved jobs by job", description = "Retrieve all applicants who saved a specific job")
    @GetMapping(value = "/job/{jobId}")
    public ResponseEntity<Object> getSavedJobsByJob(
            @PathVariable String jobId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Saved jobs retrieved successfully");
        result.put("data", savedJobService.findSavedJobsByJob(jobId, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get recent saved jobs by applicant", description = "Retrieve recent saved jobs (last 30 days) for an applicant")
    @GetMapping(value = "/applicant/{applicantId}/recent")
    public ResponseEntity<Object> getRecentSavedJobsByApplicant(
            @PathVariable String applicantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Recent saved jobs retrieved successfully");
        result.put("data", savedJobService.findRecentSavedJobsByApplicant(applicantId, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get saved jobs by date range", description = "Retrieve saved jobs by applicant within a date range")
    @GetMapping(value = "/applicant/{applicantId}/date-range")
    public ResponseEntity<Object> getSavedJobsByDateRange(
            @PathVariable String applicantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Saved jobs retrieved successfully");
        result.put("data", savedJobService.findSavedJobsByApplicantAndDateRange(applicantId, startDate, endDate, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Check if job is saved", description = "Check if a job is saved by an applicant")
    @GetMapping(value = "/check")
    public ResponseEntity<Object> isJobSaved(
            @RequestParam String applicantId,
            @RequestParam String jobId) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Check completed");
        result.put("data", savedJobService.isJobSavedByApplicant(applicantId, jobId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Count saved jobs by applicant", description = "Get count of saved jobs for an applicant")
    @GetMapping(value = "/applicant/{applicantId}/count")
    public ResponseEntity<Object> countSavedJobsByApplicant(@PathVariable String applicantId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Saved jobs count retrieved successfully");
        result.put("data", savedJobService.countSavedJobsByApplicant(applicantId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Count saved jobs by job", description = "Get count of applicants who saved a specific job")
    @GetMapping(value = "/job/{jobId}/count")
    public ResponseEntity<Object> countSavedJobsByJob(@PathVariable String jobId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Saved jobs count retrieved successfully");
        result.put("data", savedJobService.countSavedJobsByJob(jobId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "DELETE", summary = "Delete saved job", description = "Delete a saved job by its ID")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> deleteSavedJob(@PathVariable String id) {
        savedJobService.deleteSavedJob(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Saved job deleted successfully");
        result.put("data", "");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "DELETE", summary = "Unsave job", description = "Remove a saved job by applicant and job ID")
    @DeleteMapping(value = "/unsave")
    public ResponseEntity<Object> unsaveJob(
            @RequestParam String applicantId,
            @RequestParam String jobId) {
        
        savedJobService.deleteSavedJobByApplicantAndJob(applicantId, jobId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Job unsaved successfully");
        result.put("data", "");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
