package vn.iwork4se.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.iwork4se.controller.request.ApplicantSearchRequest;
import vn.iwork4se.controller.request.SavedApplicantCreationRequest;

import vn.iwork4se.elasticsearch.document.ApplicantDocument;
import vn.iwork4se.elasticsearch.service.ApplicantSearchService;

import vn.iwork4se.service.SavedApplicantService;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/saved-applicant")
@Validated
@Slf4j
@Tag(name = "Saved Applicant Controller")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class SavedApplicantController {

    private final SavedApplicantService savedApplicantService;
    private final ApplicantSearchService applicantSearchService;

    @Operation(method = "POST", summary = "Save applicant to list", description = "Add an applicant to a saved applicant list")
    @PostMapping(value = "/")
    public ResponseEntity<Object> saveApplicant(@Valid @RequestBody SavedApplicantCreationRequest request) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "Applicant saved successfully");
        result.put("data", savedApplicantService.saveApplicant(request));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(method = "GET", summary = "Get applicants by list", description = "Retrieve all saved applicants in a specific list")
    @GetMapping(value = "/list/{listId}")
    public ResponseEntity<Object> getApplicantsByList(
            @PathVariable String listId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Saved applicants retrieved successfully");
        result.put("data", savedApplicantService.getApplicantsByList(listId, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get applicants by list and contact status", description = "Retrieve saved applicants filtered by contact status")
    @GetMapping(value = "/list/{listId}/contact-status")
    public ResponseEntity<Object> getApplicantsByListAndContactStatus(
            @PathVariable String listId,
            @RequestParam Boolean isContacted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Saved applicants retrieved successfully");
        result.put("data", savedApplicantService.getApplicantsByListAndContactStatus(listId, isContacted, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get all saved applicants by employer", description = "Retrieve all saved applicants across all lists for an employer")
    @GetMapping(value = "/employer/{employerId}")
    public ResponseEntity<Object> getAllSavedApplicantsByEmployer(
            @PathVariable String employerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Saved applicants retrieved successfully");
        result.put("data", savedApplicantService.getAllSavedApplicantsByEmployer(employerId, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "PUT", summary = "Update contact status", description = "Update the contact status of a saved applicant")
    @PutMapping(value = "/{savedApplicantId}/contact-status")
    public ResponseEntity<Object> updateContactStatus(
            @PathVariable String savedApplicantId,
            @RequestParam Boolean isContacted) {

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Contact status updated successfully");
        result.put("data", savedApplicantService.updateContactStatus(savedApplicantId, isContacted));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "PUT", summary = "Update notes", description = "Update notes for a saved applicant")
    @PutMapping(value = "/{savedApplicantId}/notes")
    public ResponseEntity<Object> updateNotes(
            @PathVariable String savedApplicantId,
            @RequestParam String notes) {

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Notes updated successfully");
        result.put("data", savedApplicantService.updateNotes(savedApplicantId, notes));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Check if applicant is saved in list", description = "Check if an applicant is already saved in a specific list")
    @GetMapping(value = "/check")
    public ResponseEntity<Object> isApplicantSavedInList(
            @RequestParam String listId,
            @RequestParam String applicantId) {

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Check completed");
        result.put("data", savedApplicantService.isApplicantSavedInList(listId, applicantId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "DELETE", summary = "Remove applicant from list", description = "Remove a saved applicant by its ID")
    @DeleteMapping(value = "/{savedApplicantId}")
    public ResponseEntity<Object> removeApplicantFromList(@PathVariable String savedApplicantId) {
        savedApplicantService.removeApplicantFromList(savedApplicantId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Applicant removed from list successfully");
        result.put("data", "");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "DELETE", summary = "Remove applicant from list by IDs", description = "Remove an applicant from a list using list ID and applicant ID")
    @DeleteMapping(value = "/remove")
    public ResponseEntity<Object> removeApplicantFromListByIds(
            @RequestParam String listId,
            @RequestParam String applicantId) {

        savedApplicantService.removeApplicantFromListByIds(listId, applicantId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Applicant removed from list successfully");
        result.put("data", "");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "POST", summary = "Search saved applicants in list", description = "Search applicants within a specific saved list using Elasticsearch")
    @PostMapping(value = "/list/{listId}/search")
    public ResponseEntity<Object> searchSavedApplicants(
            @PathVariable String listId,
            @RequestBody ApplicantSearchRequest request) {

        log.info("Searching saved applicants in list: {}", listId);

        request.setSavedApplicantListId(listId);

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        Page<ApplicantDocument> results = applicantSearchService.advancedSearch(
                request.getKeywords(),
                request.getMinExperience(),
                request.getMinGpa(),
                request.getSkill(),
                request.getMajor(),
                request.getUniversity(),
                request.getGender(),
                request.getUserStatus(),
                request.getSavedApplicantListId(),
                pageable
        );

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Search completed successfully");
        result.put("data", results);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
