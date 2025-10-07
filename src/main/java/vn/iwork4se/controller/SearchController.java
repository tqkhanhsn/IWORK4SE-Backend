package vn.iwork4se.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.iwork4se.controller.request.ApplicantSearchRequest;
import vn.iwork4se.controller.request.JobPostSearchRequest;
import vn.iwork4se.elasticsearch.document.ApplicantDocument;
import vn.iwork4se.elasticsearch.document.JobPostDocument;
import vn.iwork4se.elasticsearch.service.ApplicantSearchService;
import vn.iwork4se.elasticsearch.service.JobPostSearchService;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Search", description = "Elasticsearch search endpoints")
public class SearchController {

    private final JobPostSearchService jobPostSearchService;
    private final ApplicantSearchService applicantSearchService;

    @PostMapping("/job-posts")
    @Operation(summary = "Search job posts with multi-field keywords and filters")
    public ResponseEntity<Page<JobPostDocument>> searchJobPosts(@RequestBody JobPostSearchRequest request) {
        log.info("Searching job posts with request: {}", request);

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        Page<JobPostDocument> results = jobPostSearchService.advancedSearch(
                request.getKeywords(),
                request.getJobStatus(),
                request.getJobType(),
                request.getLocation(),
                request.getMinSalary(),
                request.getMaxSalary(),
                request.getExperience(),
                request.getCategoryId(),
                request.getEmployerId(),
                request.getPostedAfter(),
                request.getClosingBefore(),
                pageable
        );

        return ResponseEntity.ok(results);
    }

//    @GetMapping("/job-posts/keywords")
//    @Operation(summary = "Simple keyword search for job posts")
//    public ResponseEntity<Page<JobPostDocument>> searchJobPostsByKeywords(
//            @RequestParam String keywords,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size
//    ) {
//        log.info("Simple keyword search for job posts: {}", keywords);
//
//        Pageable pageable = PageRequest.of(page, size);
//        Page<JobPostDocument> results = jobPostSearchService.searchByKeywords(keywords, pageable);
//
//        return ResponseEntity.ok(results);
//    }

    @PostMapping("/applicants")
    @Operation(summary = "Search applicants with multi-field keywords and filters")
    public ResponseEntity<Page<ApplicantDocument>> searchApplicants(@RequestBody ApplicantSearchRequest request) {
        log.info("Searching applicants with request: {}", request);

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
                pageable
        );

        return ResponseEntity.ok(results);
    }

//    @GetMapping("/applicants/keywords")
//    @Operation(summary = "Simple keyword search for applicants")
//    public ResponseEntity<Page<ApplicantDocument>> searchApplicantsByKeywords(
//            @RequestParam String keywords,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size
//    ) {
//        log.info("Simple keyword search for applicants: {}", keywords);
//
//        Pageable pageable = PageRequest.of(page, size);
//        Page<ApplicantDocument> results = applicantSearchService.searchByKeywords(keywords, pageable);
//
//        return ResponseEntity.ok(results);
//    }

    @PostMapping("/sync/job-posts")
    @Operation(summary = "Sync all job posts from database to Elasticsearch")
    public ResponseEntity<String> syncAllJobPosts() {
        log.info("Syncing all job posts to Elasticsearch");
        jobPostSearchService.syncAllJobPostsFromDatabase();
        return ResponseEntity.ok("Job posts synced successfully");
    }

    @PostMapping("/sync/job-posts/{id}")
    @Operation(summary = "Sync specific job post from database to Elasticsearch")
    public ResponseEntity<String> syncJobPost(@PathVariable String id) {
        log.info("Syncing job post {} to Elasticsearch", id);
        jobPostSearchService.syncJobPostFromDatabase(id);
        return ResponseEntity.ok("Job post synced successfully");
    }

    @PostMapping("/sync/applicants")
    @Operation(summary = "Sync all applicants from database to Elasticsearch")
    public ResponseEntity<String> syncAllApplicants() {
        log.info("Syncing all applicants to Elasticsearch");
        applicantSearchService.syncAllApplicantsFromDatabase();
        return ResponseEntity.ok("Applicants synced successfully");
    }

    @PostMapping("/sync/applicants/{id}")
    @Operation(summary = "Sync specific applicant from database to Elasticsearch")
    public ResponseEntity<String> syncApplicant(@PathVariable String id) {
        log.info("Syncing applicant {} to Elasticsearch", id);
        applicantSearchService.syncApplicantFromDatabase(id);
        return ResponseEntity.ok("Applicant synced successfully");
    }

    @PostMapping("/job-posts/resync-all")
    public ResponseEntity<String> resyncAllJobPosts() {
        try {
            jobPostSearchService.deleteAllAndResync();
            return ResponseEntity.ok("All job posts deleted and resynced successfully");
        } catch (Exception e) {
            log.error("Error resyncing all job posts: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error resyncing: " + e.getMessage());
        }
    }



    @PostMapping("/applicants/resync-all")
    public ResponseEntity<String> resyncAllApplicants() {
        try {
            applicantSearchService.deleteAllAndResync();
            return ResponseEntity.ok("All applicants deleted and resynced successfully");
        } catch (Exception e) {
            log.error("Error resyncing all applicants: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error resyncing: " + e.getMessage());
        }
    }
}
