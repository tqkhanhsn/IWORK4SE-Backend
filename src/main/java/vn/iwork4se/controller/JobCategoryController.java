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
import vn.iwork4se.controller.request.JobCategoryCreationRequest;
import vn.iwork4se.controller.request.JobCategoryUpdateRequest;
import vn.iwork4se.service.JobCategoryService;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/job-category")
@Validated
@Slf4j
@Tag(name = "Job Category Controller")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class JobCategoryController {
    private final JobCategoryService jobCategoryService;

    @Operation(method = "POST", summary = "Add new Job Category", description = "Send a request via this API to create new job category")
    @PostMapping(value = "/")
    public ResponseEntity<Object> createJobCategory(@Valid @RequestBody JobCategoryCreationRequest request) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "Job category has been successfully created");
        result.put("data", jobCategoryService.save(request));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(method = "PUT", summary = "Update Job Category", description = "Send a request via this API to update job category")
    @PutMapping(value = "/update")
    public Map<String, Object> updateJobCategory(@RequestBody JobCategoryUpdateRequest request) {
        log.info("Updating job category with request: {}", request);
        jobCategoryService.updateJobCategory(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.ACCEPTED.value());
        result.put("message", "Job Category has been successfully updated");
        result.put("data", "");
        return result;
    }

    @Operation(method = "GET", summary = "Get Job Category by ID", description = "Retrieve a specific job category by its ID")
    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getJobCategoryById(@PathVariable Long id) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Job category retrieved successfully");
        result.put("data", jobCategoryService.findJobCategoryById(id));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get All Job Categories", description = "Retrieve all job categories with optional search and pagination")
    @GetMapping(value = "/")
    public ResponseEntity<Object> getAllJobCategories(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Job categories retrieved successfully");
        result.put("data", jobCategoryService.findAllJobCategories(keyword, sort, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get All Job Categories (No Pagination)", description = "Retrieve all job categories without pagination")
    @GetMapping(value = "/all")
    public ResponseEntity<Object> getAllJobCategoriesNoPagination() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Job categories retrieved successfully");
        result.put("data", jobCategoryService.getAllJobCategories());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "DELETE", summary = "Delete Job Category", description = "Delete a job category by its ID")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> deleteJobCategory(@PathVariable Long id) {
        jobCategoryService.deleteJobCategory(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Job category has been successfully deleted");
        result.put("data", "");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get Job Categories by Name", description = "Retrieve job categories by name containing")
    @GetMapping(value = "/name/{categoryName}")
    public ResponseEntity<Object> getJobCategoriesByName(
            @PathVariable String categoryName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Job categories retrieved successfully");
        result.put("data", jobCategoryService.findJobCategoriesByName(categoryName, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get Job Category by Name (Exact)", description = "Retrieve a specific job category by its exact name")
    @GetMapping(value = "/exact-name/{categoryName}")
    public ResponseEntity<Object> getJobCategoryByName(@PathVariable String categoryName) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Job category retrieved successfully");
        result.put("data", jobCategoryService.findJobCategoryByName(categoryName));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get Job Categories by Description", description = "Retrieve job categories by description containing")
    @GetMapping(value = "/description/{description}")
    public ResponseEntity<Object> getJobCategoriesByDescription(
            @PathVariable String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Job categories retrieved successfully");
        result.put("data", jobCategoryService.findJobCategoriesByDescription(description, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get Job Categories with Most Job Posts", description = "Retrieve job categories ordered by number of job posts")
    @GetMapping(value = "/most-popular")
    public ResponseEntity<Object> getJobCategoriesWithMostJobPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Job categories retrieved successfully");
        result.put("data", jobCategoryService.findJobCategoriesWithMostJobPosts(page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Count Job Posts by Category", description = "Get count of job posts by category ID")
    @GetMapping(value = "/{id}/job-posts/count")
    public ResponseEntity<Object> countJobPostsByCategory(@PathVariable Long id) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Job post count retrieved successfully");
        result.put("data", jobCategoryService.countJobPostsByCategory(id));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Check if Category Name Exists", description = "Check if a category name already exists")
    @GetMapping(value = "/exists/name/{categoryName}")
    public ResponseEntity<Object> checkCategoryNameExists(@PathVariable String categoryName) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Category name check completed");
        result.put("data", jobCategoryService.existsByCategoryName(categoryName));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Check if Category Name Exists (Excluding ID)", description = "Check if a category name already exists excluding a specific category ID")
    @GetMapping(value = "/exists/name/{categoryName}/excluding/{excludeId}")
    public ResponseEntity<Object> checkCategoryNameExistsExcludingId(
            @PathVariable String categoryName,
            @PathVariable Long excludeId) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Category name check completed");
        result.put("data", jobCategoryService.existsByCategoryNameExcludingId(categoryName, excludeId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
