package vn.iwork4se.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.iwork4se.controller.request.SavedApplicantListCreationRequest;
import vn.iwork4se.service.SavedApplicantListService;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/saved-applicant-list")
@Validated
@Slf4j
@Tag(name = "Saved Applicant List Controller")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class SavedApplicantListController {

    private final SavedApplicantListService savedApplicantListService;

    @Operation(method = "POST", summary = "Create saved applicant list", description = "Create a new saved applicant list for an employer")
    @PostMapping(value = "/")
    public ResponseEntity<Object> createList(@Valid @RequestBody SavedApplicantListCreationRequest request) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "List created successfully");
        result.put("data", savedApplicantListService.createList(request));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(method = "POST", summary = "Create default list", description = "Create default saved applicant list for an employer")
    @PostMapping(value = "/default")
    public ResponseEntity<Object> createDefaultList(@RequestParam String employerId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "Default list created successfully");
        result.put("data", savedApplicantListService.createDefaultList(employerId));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(method = "GET", summary = "Get all lists by employer", description = "Retrieve all saved applicant lists for an employer")
    @GetMapping(value = "/employer/{employerId}")
    public ResponseEntity<Object> getAllListsByEmployer(@PathVariable String employerId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Lists retrieved successfully");
        result.put("data", savedApplicantListService.getAllListsByEmployer(employerId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get list by ID", description = "Retrieve a specific saved applicant list by its ID")
    @GetMapping(value = "/{listId}")
    public ResponseEntity<Object> getListById(@PathVariable String listId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "List retrieved successfully");
        result.put("data", savedApplicantListService.getListById(listId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "DELETE", summary = "Delete list", description = "Delete a saved applicant list (cannot delete default list)")
    @DeleteMapping(value = "/{listId}")
    public ResponseEntity<Object> deleteList(@PathVariable String listId) {
        savedApplicantListService.deleteList(listId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "List deleted successfully");
        result.put("data", "");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
