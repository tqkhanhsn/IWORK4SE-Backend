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
import vn.iwork4se.controller.request.JobPostCreationRequest;
import vn.iwork4se.controller.request.JobPostUpdateRequest;
import vn.iwork4se.service.JobPostService;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/job-post")
@Validated
@Slf4j
@Tag(name = "Job Post Controller")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class JobPostController {
    private final JobPostService jobPostService;

    @Operation(method = "POST", summary = "Add new Job Post", description = "Send a request via this API to create new job post")
    @PostMapping(value = "/")
    public ResponseEntity<Object> createJobPost(@Valid @RequestBody JobPostCreationRequest request) {
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "Job post has been successfully created");
        result.put("data", jobPostService.save(request));
        return new ResponseEntity<>(result,HttpStatus.CREATED);
    }

    @Operation(method ="UPDATE", summary = "Update Job Post", description = "Send a request via this API to update job post")
    @PutMapping(value = "/update")
    public Map<String, Object> updateJobPost(@RequestBody JobPostUpdateRequest request) {
        log.info("Updating job post with request: {}", request);
        jobPostService.updateJobPost(request);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.ACCEPTED.value());
        result.put("message", "Job Post has been successfully updated");
        result.put("data", "");
        return result;
    }

    @Operation(method = "GET", summary = "Get Job Post by ID", description = "Retrieve a specific job post by its ID")
    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> getJobPostById(@PathVariable String id) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Job post retrieved successfully");
        result.put("data", jobPostService.findJobPostById(id));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get All Job Posts", description = "Retrieve all job posts with optional search and pagination")
    @GetMapping(value = "/")
    public ResponseEntity<Object> getAllJobPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Job posts retrieved successfully");
        result.put("data", jobPostService.findAllJobPosts(keyword, sort, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "DELETE", summary = "Delete Job Post", description = "Delete a job post by its ID")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> deleteJobPost(@PathVariable String id) {
        jobPostService.deleteJobPost(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Job post has been successfully deleted");
        result.put("data", "");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get Job Posts by Employer", description = "Retrieve job posts by employer ID")
    @GetMapping(value = "/employer/{employerId}")
    public ResponseEntity<Object> getJobPostsByEmployer(
            @PathVariable String employerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Job posts retrieved successfully");
        result.put("data", jobPostService.findJobPostsByEmployer(employerId, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get Active Job Posts", description = "Retrieve only active (non-expired) job posts")
    @GetMapping(value = "/active")
    public ResponseEntity<Object> getActiveJobPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Active job posts retrieved successfully");
        result.put("data", jobPostService.findActiveJobPosts(keyword, sort, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get Job Posts by Status", description = "Retrieve job posts by status")
    @GetMapping(value = "/status/{status}")
    public ResponseEntity<Object> getJobPostsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Job posts retrieved successfully");
        result.put("data", jobPostService.findJobPostsByStatus(status, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get Job Posts by Type", description = "Retrieve job posts by job type")
    @GetMapping(value = "/type/{jobType}")
    public ResponseEntity<Object> getJobPostsByType(
            @PathVariable String jobType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Job posts retrieved successfully");
        result.put("data", jobPostService.findJobPostsByType(jobType, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get Job Posts by Location", description = "Retrieve job posts by location")
    @GetMapping(value = "/location/{location}")
    public ResponseEntity<Object> getJobPostsByLocation(
            @PathVariable String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Job posts retrieved successfully");
        result.put("data", jobPostService.findJobPostsByLocation(location, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get Job Posts by Salary Range", description = "Retrieve job posts by salary range")
    @GetMapping(value = "/salary-range")
    public ResponseEntity<Object> getJobPostsBySalaryRange(
            @RequestParam Double minSalary,
            @RequestParam Double maxSalary,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Job posts retrieved successfully");
        result.put("data", jobPostService.findJobPostsBySalaryRange(minSalary, maxSalary, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Count Job Posts by Employer", description = "Get count of job posts by employer")
    @GetMapping(value = "/employer/{employerId}/count")
    public ResponseEntity<Object> countJobPostsByEmployer(@PathVariable String employerId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Job post count retrieved successfully");
        result.put("data", jobPostService.countJobPostsByEmployer(employerId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Get Job Posts by Category", description = "Retrieve job posts by category")
    @GetMapping(value = "/category/{categoryId}")
    public ResponseEntity<Object> getJobPostsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Job posts retrieved successfully");
        result.put("data", jobPostService.findJobPostsByCategory(categoryId, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "PUT", summary = "Update Job Post Status", description = "Update the status of a job post")
    @PutMapping(value = "/{id}/status")
    public ResponseEntity<Object> updateJobPostStatus(
            @PathVariable String id,
            @RequestParam String status) {
        
        jobPostService.updateJobPostStatus(id, status);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Job post status updated successfully");
        result.put("data", "");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(method = "GET", summary = "Search Job Posts with Multiple Criteria", description = "Search job posts with multiple filtering criteria")
    @GetMapping(value = "/search")
    public ResponseEntity<Object> searchJobPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Job posts retrieved successfully");
        result.put("data", jobPostService.searchJobPostsWithMultipleCriteria(
                keyword, status, jobType, location, minSalary, maxSalary, sort, page, size));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
