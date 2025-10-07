package vn.iwork4se.elasticsearch.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import vn.iwork4se.common.JobStatus;
import vn.iwork4se.common.JobType;
import vn.iwork4se.elasticsearch.document.JobPostDocument;
import vn.iwork4se.elasticsearch.repository.JobPostElasticsearchRepository;
import vn.iwork4se.elasticsearch.service.JobPostSearchService;
import vn.iwork4se.model.JobPost;
import vn.iwork4se.repository.JobPostRepository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobPostSearchServiceImpl implements JobPostSearchService {

    private final JobPostElasticsearchRepository elasticsearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final JobPostRepository jobPostRepository;
    private final EntityManager entityManager;

    @Override
    public JobPostDocument indexJobPost(JobPostDocument document) {
        log.info("Indexing job post: {}", document.getId());
        return elasticsearchRepository.save(document);
    }

    @Override
    public void deleteJobPost(String id) {
        log.info("Deleting job post from index: {}", id);
        elasticsearchRepository.deleteById(id);
    }

    @Override
    public Page<JobPostDocument> searchByKeywords(String keywords, Pageable pageable) {
        log.info("Searching job posts with keywords: {}", keywords);

        if (keywords == null || keywords.trim().isEmpty()) {
            return elasticsearchRepository.findAll(pageable);
        }

        Query query = Query.of(q -> q.match(m -> m
                .field("searchableText")
                .query(keywords)
                .operator(Operator.And) // All keywords must be present
        ));

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(query)
                .withPageable(pageable)
                .build();

        SearchHits<JobPostDocument> searchHits = elasticsearchOperations.search(searchQuery, JobPostDocument.class);

        List<JobPostDocument> documents = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        return new PageImpl<>(documents, pageable, searchHits.getTotalHits());
    }

    @Override
    public Page<JobPostDocument> advancedSearch(
            String keywords,
            JobStatus jobStatus,
            JobType jobType,
            String location,
            Double minSalary,
            Double maxSalary,
            String experience,
            Long categoryId,
            String employerId,
            LocalDate postedAfter,
            LocalDate closingBefore,
            Pageable pageable
    ) {
        log.info("Advanced search with filters - keywords: {}, status: {}, type: {}", keywords, jobStatus, jobType);

        List<Query> mustQueries = new ArrayList<>();
        List<Query> filterQueries = new ArrayList<>();

        if (keywords != null && !keywords.trim().isEmpty()) {
            mustQueries.add(Query.of(q -> q.match(m -> m
                    .field("searchableText")
                    .query(keywords)
                    .operator(Operator.And) // All keywords must be present
            )));
        }

        // Job status filter
        if (jobStatus != null) {
            filterQueries.add(Query.of(q -> q.term(t -> t.field("jobStatus").value(jobStatus.name()))));
        }

        // Job type filter
        if (jobType != null) {
            filterQueries.add(Query.of(q -> q.term(t -> t.field("jobType").value(jobType.name()))));
        }

        // Location filter
        if (location != null && !location.trim().isEmpty()) {
            filterQueries.add(Query.of(q -> q.match(m -> m.field("location").query(location))));
        }

        // Salary range filter
        if (minSalary != null || maxSalary != null) {
            filterQueries.add(Query.of(q -> q.range(r -> r
                    .number(n -> {
                        var builder = n.field("minSalary");
                        if (minSalary != null) {
                            builder = builder.gte(minSalary);
                        }
                        if (maxSalary != null) {
                            builder = builder.lte(maxSalary);
                        }
                        return builder;
                    })
            )));
        }

        // Experience filter
        if (experience != null && !experience.trim().isEmpty()) {
            filterQueries.add(Query.of(q -> q.match(m -> m.field("experience").query(experience))));
        }

        // Category filter
        if (categoryId != null) {
            filterQueries.add(Query.of(q -> q.term(t -> t.field("categoryId").value(categoryId))));
        }

        // Employer filter
        if (employerId != null && !employerId.trim().isEmpty()) {
            filterQueries.add(Query.of(q -> q.term(t -> t.field("employerId").value(employerId))));
        }

        // Posted date filter
        if (postedAfter != null) {
            filterQueries.add(Query.of(q -> q.range(r -> r
                    .date(d -> d
                            .field("postedDate")
                            .gte(postedAfter.toString())
                    )
            )));
        }

        // Closing date filter
        if (closingBefore != null) {
            filterQueries.add(Query.of(q -> q.range(r -> r
                    .date(d -> d
                            .field("closingDate")
                            .lte(closingBefore.toString())
                    )
            )));
        }

        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        if (!mustQueries.isEmpty()) {
            boolQueryBuilder.must(mustQueries);
        }

        if (!filterQueries.isEmpty()) {
            boolQueryBuilder.filter(filterQueries);
        }

        // If no queries at all, return all
        if (mustQueries.isEmpty() && filterQueries.isEmpty()) {
            return elasticsearchRepository.findAll(pageable);
        }

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(Query.of(q -> q.bool(boolQueryBuilder.build())))
                .withPageable(pageable)
                .build();

        try {
            SearchHits<JobPostDocument> searchHits = elasticsearchOperations.search(searchQuery, JobPostDocument.class);

            List<JobPostDocument> documents = searchHits.getSearchHits().stream()
                    .map(hit -> {
                        try {
                            return hit.getContent();
                        } catch (Exception e) {
                            log.error("Error converting document with id: {}, error: {}", hit.getId(), e.getMessage());
                            return null;
                        }
                    })
                    .filter(doc -> doc != null)
                    .collect(Collectors.toList());

            return new PageImpl<>(documents, pageable, searchHits.getTotalHits());
        } catch (Exception e) {
            log.error("Error during search: {}", e.getMessage(), e);
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
    }

    @Override
    public void syncJobPostFromDatabase(String jobPostId) {
        log.info("Syncing job post from database: {}", jobPostId);

        try {
            JobPostDocument document = fetchJobPostDataWithSQL(jobPostId);
            if (document != null) {
                elasticsearchRepository.save(document);
                log.info("Job post synced successfully: {}", jobPostId);
            } else {
                log.warn("Job post not found: {}", jobPostId);
            }
        } catch (Exception e) {
            log.error("Error syncing job post {}: {}", jobPostId, e.getMessage(), e);
            throw new RuntimeException("Failed to sync job post: " + jobPostId, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void syncAllJobPostsFromDatabase() {
        log.info("Syncing all job posts from database using native SQL");

        try {
            List<String> jobPostIds = entityManager.createNativeQuery(
                    "SELECT DISTINCT id FROM tbl_job_post"
            ).getResultList();

            log.info("Found {} job post IDs to sync", jobPostIds.size());

            List<JobPostDocument> documents = new ArrayList<>();
            int successCount = 0;
            int errorCount = 0;

            for (String id : jobPostIds) {
                try {
                    JobPostDocument document = fetchJobPostDataWithSQL(id);
                    if (document != null) {
                        documents.add(document);
                        successCount++;

                        // Save in batches of 100
                        if (documents.size() >= 100) {
                            elasticsearchRepository.saveAll(documents);
                            log.info("Saved batch of {} job posts", documents.size());
                            documents.clear();
                        }
                    }
                } catch (Exception e) {
                    errorCount++;
                    log.error("Error syncing job post {}: {}", id, e.getMessage());
                }
            }

            // Save remaining documents
            if (!documents.isEmpty()) {
                elasticsearchRepository.saveAll(documents);
                log.info("Saved final batch of {} job posts", documents.size());
            }

            log.info("Synced {} job posts successfully, {} errors", successCount, errorCount);
        } catch (Exception e) {
            log.error("Error during sync all: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to sync all job posts", e);
        }
    }

    @Override
    public void deleteAllAndResync() {
        log.info("Deleting all job posts from Elasticsearch and resyncing");
        try {
            elasticsearchRepository.deleteAll();
            log.info("All job posts deleted from Elasticsearch");

            Thread.sleep(2000); // Wait for deletion to complete

            syncAllJobPostsFromDatabase();
            log.info("All job posts resynced successfully");
        } catch (Exception e) {
            log.error("Error during delete and resync: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete and resync", e);
        }
    }
    @Override
    @SuppressWarnings("unchecked")
    public void syncJobPostsByEmployerId(String employerId) {
        log.info("Syncing job posts for employer: {}", employerId);

        try {
            // Query job post IDs directly from database without accessing lazy collections
            List<String> jobPostIds = entityManager.createNativeQuery(
                    "SELECT id FROM tbl_job_post WHERE employer_id = ?1"
            ).setParameter(1, employerId).getResultList();

            log.info("Found {} job posts for employer {}", jobPostIds.size(), employerId);

            for (String jobPostId : jobPostIds) {
                try {
                    syncJobPostFromDatabase(jobPostId);
                } catch (Exception e) {
                    log.error("Error syncing job post {} for employer {}: {}", jobPostId, employerId, e.getMessage());
                }
            }

            log.info("Completed syncing job posts for employer: {}", employerId);
        } catch (Exception e) {
            log.error("Error syncing job posts for employer {}: {}", employerId, e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private JobPostDocument fetchJobPostDataWithSQL(String jobPostId) {
        try {
            entityManager.setFlushMode(FlushModeType.COMMIT);
            // Fetch job post data with employer and category info
            String sql = """
                SELECT DISTINCT 
                    jp.id, jp.title, jp.description, jp.job_position, jp.location,
                    jp.experience, jp.min_salary, jp.max_salary, jp.posted_date,
                    jp.closing_date, jp.vacancies, jp.job_status, jp.job_type, jp.update_at,
                    e.user_id as employer_id, u.first_name, u.last_name, e.company_name,
                    e.logo_url,
                    c.id as category_id, c.category_name
                FROM tbl_job_post jp
                LEFT JOIN tbl_employer e ON jp.employer_id = e.user_id
                LEFT JOIN tbl_users u ON e.user_id = u.id
                LEFT JOIN tbl_job_category c ON jp.category_id = c.id
                WHERE jp.id = ?1
                LIMIT 1
                """;

            List<Object[]> results = entityManager.createNativeQuery(sql)
                    .setParameter(1, jobPostId)
                    .getResultList();

            if (results.isEmpty()) {
                log.warn("Job post not found: {}", jobPostId);
                return null;
            }

            Object[] row = results.get(0);
            String id = (String) row[0];
            String title = (String) row[1];
            String description = (String) row[2];
            String jobPosition = (String) row[3];
            String location = (String) row[4];
            String experience = (String) row[5];
            BigDecimal minSalary = row[6] != null ? new BigDecimal(row[6].toString()) : null;
            BigDecimal maxSalary = row[7] != null ? new BigDecimal(row[7].toString()) : null;
            LocalDate postedDate = row[8] != null ? convertToLocalDate(row[8]) : null;
            LocalDate closingDate = row[9] != null ? convertToLocalDate(row[9]) : null;
            Integer vacancies = (Integer) row[10];
            String jobStatusStr = (String) row[11];
            String jobTypeStr = (String) row[12];
            LocalDate updateAt = row[13] != null ? convertToLocalDate(row[13]) : null;
            String employerId = (String) row[14];
            String employerFirstName = (String) row[15];
            String employerLastName = (String) row[16];
            String companyName = (String) row[17];
            String logoUrl = (String) row[18];
            Long categoryId = row[19] != null ? ((Number) row[19]).longValue() : null;
            String categoryName = (String) row[20];

            // Build searchable text
            StringBuilder searchableText = new StringBuilder();
            if (title != null) searchableText.append(title).append(" ");
            if (description != null) searchableText.append(description).append(" ");
            if (jobPosition != null) searchableText.append(jobPosition).append(" ");
            if (location != null) searchableText.append(location).append(" ");
            if (experience != null) searchableText.append(experience).append(" ");
            if (employerFirstName != null) searchableText.append(employerFirstName).append(" ");
            if (employerLastName != null) searchableText.append(employerLastName).append(" ");
            if (companyName != null) searchableText.append(companyName).append(" ");
            if (categoryName != null) searchableText.append(categoryName).append(" ");

            String employerName = null;
            if (employerFirstName != null && employerLastName != null) {
                employerName = employerFirstName + " " + employerLastName;
            }

            return JobPostDocument.builder()
                    .id(id)
                    .title(title)
                    .description(description)
                    .jobPosition(jobPosition)
                    .location(location)
                    .experience(experience)
                    .minSalary(minSalary != null ? minSalary.doubleValue() : null)
                    .maxSalary(maxSalary != null ? maxSalary.doubleValue() : null)
                    .postedDate(postedDate)
                    .closingDate(closingDate)
                    .vacancies(vacancies)
                    .jobStatus(jobStatusStr != null ? JobStatus.valueOf(jobStatusStr) : null)
                    .jobType(jobTypeStr != null ? JobType.valueOf(jobTypeStr) : null)
                    .updateAt(updateAt)
                    .employerId(employerId)
                    .employerName(employerName)
                    .companyName(companyName)
                    .logoUrl(logoUrl)
                    .categoryId(categoryId)
                    .categoryName(categoryName)
                    .searchableText(searchableText.toString().trim())
                    .build();

        } catch (Exception e) {
            log.error("Error fetching job post data with SQL for {}: {}", jobPostId, e.getMessage(), e);
            return null;
        }
    }

    private LocalDate convertToLocalDate(Object dateObject) {
        if (dateObject instanceof Timestamp) {
            return ((Timestamp) dateObject).toLocalDateTime().toLocalDate();
        } else if (dateObject instanceof Date) {
            return ((Date) dateObject).toLocalDate();
        } else if (dateObject instanceof java.util.Date) {
            return new java.sql.Date(((java.util.Date) dateObject).getTime()).toLocalDate();
        }
        return null;
    }
}
