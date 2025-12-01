package vn.iwork4se.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vn.iwork4se.common.JobStatus;
import vn.iwork4se.model.JobPost;
import vn.iwork4se.repository.JobPostRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobPostStatusScheduler {

    private final JobPostRepository jobPostRepository;

    /**
     * Chạy mỗi ngày lúc 01:00 để tự động chuyển trạng thái các tin đã quá hạn sang EXPIRED.
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void updateExpiredJobPosts() {
        LocalDate today = LocalDate.now();
        List<JobPost> jobPosts = jobPostRepository.findJobPostsToExpire(today, JobStatus.EXPIRED, JobStatus.DELETED);

        if (jobPosts.isEmpty()) {
            return;
        }

        log.info("Found {} job posts to mark as EXPIRED", jobPosts.size());

        for (JobPost jobPost : jobPosts) {
            jobPost.setJobStatus(JobStatus.EXPIRED);
            jobPost.setUpdateAt(LocalDateTime.now());
        }

        jobPostRepository.saveAll(jobPosts);
        log.info("Updated {} job posts to EXPIRED status", jobPosts.size());
    }
}


