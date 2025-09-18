package vn.iwork4se.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.iwork4se.controller.request.JobPostCreationRequest;
import vn.iwork4se.service.JobPostService;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/job-post")
@Validated
@Slf4j
@Tag(name = "Job Post Controller")
@RequiredArgsConstructor
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


}
