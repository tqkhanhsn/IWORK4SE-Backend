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
import vn.iwork4se.controller.request.CVCreationRequest;
import vn.iwork4se.controller.request.CVUpdateRequest;
import vn.iwork4se.service.CVService;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/cv")
@Validated
@Slf4j
@Tag(name = "CV Controller")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class CVController {
    private final CVService cvService;

    @Operation(method = "POST", summary = "Upload CV", description = "Upload a new CV for an applicant")
    @PostMapping(value = "/")
    public ResponseEntity<Object> uploadCV(@Valid @RequestBody CVCreationRequest request) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "CV uploaded successfully");
        result.put("data", cvService.save(request));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(method = "PUT", summary = "Update CV", description = "Update an existing CV")
    @PutMapping(value = "/update")
    public ResponseEntity<Object> updateCV(@Valid @RequestBody CVUpdateRequest request) {
        cvService.updateCV(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "CV updated successfully");
        result.put("data", "");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get CV by ID", description = "Retrieve a specific CV by its ID")
    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getCVById(@PathVariable String id) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "CV retrieved successfully");
        result.put("data", cvService.findCVById(id));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get CVs by applicant", description = "Retrieve all CVs for a specific applicant")
    @GetMapping(value = "/applicant/{applicantId}")
    public ResponseEntity<Object> getCVsByApplicant(
            @PathVariable String applicantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "CVs retrieved successfully");
        result.put("data", cvService.findCVsByApplicantOrderByDate(applicantId, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get latest CV by applicant", description = "Retrieve the latest CV for an applicant")
    @GetMapping(value = "/applicant/{applicantId}/latest")
    public ResponseEntity<Object> getLatestCVByApplicant(@PathVariable String applicantId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Latest CV retrieved successfully");
        result.put("data", cvService.findLatestCVByApplicant(applicantId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get unused CVs by applicant", description = "Retrieve CVs that are not used in any application")
    @GetMapping(value = "/applicant/{applicantId}/unused")
    public ResponseEntity<Object> getUnusedCVsByApplicant(@PathVariable String applicantId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Unused CVs retrieved successfully");
        result.put("data", cvService.findUnusedCVsByApplicant(applicantId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get CVs used in applications", description = "Retrieve CVs that are used in applications")
    @GetMapping(value = "/used-in-applications")
    public ResponseEntity<Object> getCVsUsedInApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "CVs used in applications retrieved successfully");
        result.put("data", cvService.findCVsUsedInApplications(page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get recent CVs", description = "Retrieve recent CVs (last 30 days)")
    @GetMapping(value = "/recent")
    public ResponseEntity<Object> getRecentCVs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Recent CVs retrieved successfully");
        result.put("data", cvService.findRecentCVs(page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get CVs by date range", description = "Retrieve CVs within a date range")
    @GetMapping(value = "/date-range")
    public ResponseEntity<Object> getCVsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "CVs retrieved successfully");
        result.put("data", cvService.findCVsByDateRange(startDate, endDate, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get CVs by applicant and date range", description = "Retrieve CVs by applicant within a date range")
    @GetMapping(value = "/applicant/{applicantId}/date-range")
    public ResponseEntity<Object> getCVsByApplicantAndDateRange(
            @PathVariable String applicantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "CVs retrieved successfully");
        result.put("data", cvService.findCVsByApplicantAndDateRange(applicantId, startDate, endDate, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get CVs by URL pattern", description = "Retrieve CVs by URL pattern (for file management)")
    @GetMapping(value = "/url-pattern")
    public ResponseEntity<Object> getCVsByUrlPattern(@RequestParam String urlPattern) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "CVs retrieved successfully");
        result.put("data", cvService.findCVsByUrlPattern(urlPattern));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Validate CV URL", description = "Check if a CV URL is valid")
    @GetMapping(value = "/validate-url")
    public ResponseEntity<Object> validateCVUrl(@RequestParam String url) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "URL validation completed");
        result.put("data", cvService.isValidCVUrl(url));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Check CV ownership", description = "Check if a CV is owned by a specific applicant")
    @GetMapping(value = "/{cvId}/ownership")
    public ResponseEntity<Object> checkCVOwnership(
            @PathVariable String cvId,
            @RequestParam String applicantId) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Ownership check completed");
        result.put("data", cvService.isCVOwnedByApplicant(cvId, applicantId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Count CVs by applicant", description = "Get count of CVs for an applicant")
    @GetMapping(value = "/applicant/{applicantId}/count")
    public ResponseEntity<Object> countCVsByApplicant(@PathVariable String applicantId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "CVs count retrieved successfully");
        result.put("data", cvService.countCVsByApplicant(applicantId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "DELETE", summary = "Delete CV", description = "Delete a CV by its ID")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> deleteCV(@PathVariable String id) {
        cvService.deleteCV(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "CV deleted successfully");
        result.put("data", "");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "DELETE", summary = "Delete CVs by URL pattern", description = "Delete CVs by URL pattern (for cleanup)")
    @DeleteMapping(value = "/url-pattern")
    public ResponseEntity<Object> deleteCVsByUrlPattern(@RequestParam String urlPattern) {
        cvService.deleteCVsByUrlPattern(urlPattern);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "CVs deleted successfully");
        result.put("data", "");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
