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
import vn.iwork4se.common.Gender;
import vn.iwork4se.common.UserStatus;
import vn.iwork4se.elasticsearch.document.ApplicantDocument;
import vn.iwork4se.elasticsearch.repository.ApplicantElasticsearchRepository;
import vn.iwork4se.elasticsearch.service.ApplicantSearchService;
import vn.iwork4se.model.Applicant;
import vn.iwork4se.model.Certificate;
import vn.iwork4se.repository.ApplicantRepository;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicantSearchServiceImpl implements ApplicantSearchService {

    private final ApplicantElasticsearchRepository elasticsearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ApplicantRepository applicantRepository;
    private final EntityManager entityManager;

    @Override
    public ApplicantDocument indexApplicant(ApplicantDocument document) {
        log.info("Indexing applicant: {}", document.getId());
        return elasticsearchRepository.save(document);
    }

    @Override
    public void deleteApplicant(String id) {
        log.info("Deleting applicant from index: {}", id);
        elasticsearchRepository.deleteById(id);
    }

    @Override
    public Page<ApplicantDocument> searchByKeywords(String keywords, Pageable pageable) {
        log.info("Searching applicants with keywords: {}", keywords);

        if (keywords == null || keywords.trim().isEmpty()) {
            return elasticsearchRepository.findByUserStatus(UserStatus.ACTIVE, pageable);
        }

        BoolQuery boolQuery = BoolQuery.of(b -> b
                .must(Query.of(q -> q.match(m -> m
                        .field("searchableText")
                        .query(keywords)
                        .operator(Operator.And) // All keywords must be present
                )))
                .filter(Query.of(q -> q.term(t -> t.field("userStatus").value(UserStatus.ACTIVE.name()))))
        );

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(Query.of(q -> q.bool(boolQuery)))
                .withPageable(pageable)
                .build();

        SearchHits<ApplicantDocument> searchHits = elasticsearchOperations.search(searchQuery, ApplicantDocument.class);

        List<ApplicantDocument> documents = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        return new PageImpl<>(documents, pageable, searchHits.getTotalHits());
    }

    @Override
    public Page<ApplicantDocument> advancedSearch(
            String keywords,
            Integer minExperience,
            Double minGpa,
            String skill,
            String major,
            String university,
            Gender gender,
            UserStatus userStatus,
            Pageable pageable
    ) {
        log.info("Advanced search applicants - keywords: {}, minExp: {}, minGpa: {}", keywords, minExperience, minGpa);

        List<Query> mustQueries = new ArrayList<>();
        List<Query> filterQueries = new ArrayList<>();

        // User status filter (default to ACTIVE)
        UserStatus status = userStatus != null ? userStatus : UserStatus.ACTIVE;
        filterQueries.add(Query.of(q -> q.term(t -> t.field("userStatus").value(status.name()))));

        if (keywords != null && !keywords.trim().isEmpty()) {
            mustQueries.add(Query.of(q -> q.match(m -> m
                    .field("searchableText")
                    .query(keywords)
                    .operator(Operator.And) // All keywords must be present
            )));
        }

        // Experience filter
        if (minExperience != null) {
            filterQueries.add(Query.of(q -> q.range(r -> r
                    .number(n -> n
                            .field("yearsOfExperience")
                            .gte(minExperience.doubleValue())
                    )
            )));
        }

        // GPA filter
        if (minGpa != null) {
            filterQueries.add(Query.of(q -> q.range(r -> r
                    .number(n -> n
                            .field("gpa")
                            .gte(minGpa)
                    )
            )));
        }

        // Skill filter
        if (skill != null && !skill.trim().isEmpty()) {
            filterQueries.add(Query.of(q -> q.match(m -> m.field("skills").query(skill))));
        }

        // Major filter
        if (major != null && !major.trim().isEmpty()) {
            filterQueries.add(Query.of(q -> q.match(m -> m.field("major").query(major))));
        }

        // University filter
        if (university != null && !university.trim().isEmpty()) {
            filterQueries.add(Query.of(q -> q.match(m -> m.field("universityName").query(university))));
        }

        // Gender filter
        if (gender != null) {
            filterQueries.add(Query.of(q -> q.term(t -> t.field("gender").value(gender.name()))));
        }

        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        if (!mustQueries.isEmpty()) {
            boolQueryBuilder.must(mustQueries);
        }

        if (!filterQueries.isEmpty()) {
            boolQueryBuilder.filter(filterQueries);
        }

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(Query.of(q -> q.bool(boolQueryBuilder.build())))
                .withPageable(pageable)
                .build();

        try {
            SearchHits<ApplicantDocument> searchHits = elasticsearchOperations.search(searchQuery, ApplicantDocument.class);

            List<ApplicantDocument> documents = searchHits.getSearchHits().stream()
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
    public void syncApplicantFromDatabase(String applicantId) {
        log.info("Syncing applicant from database: {}", applicantId);

        try {
            ApplicantDocument document = fetchApplicantDataWithSQL(applicantId);
            if (document != null) {
                elasticsearchRepository.save(document);
                log.info("Applicant synced successfully: {}", applicantId);
            } else {
                log.warn("Applicant not found: {}", applicantId);
            }
        } catch (Exception e) {
            log.error("Error syncing applicant {}: {}", applicantId, e.getMessage(), e);
            throw new RuntimeException("Failed to sync applicant: " + applicantId, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void syncAllApplicantsFromDatabase() {
        log.info("Syncing all applicants from database using native SQL");

        try {
            List<String> applicantIds = entityManager.createNativeQuery(
                    "SELECT DISTINCT u.id FROM tbl_users u INNER JOIN tbl_applicant a ON u.id = a.user_id"
            ).getResultList();

            log.info("Found {} applicant IDs to sync", applicantIds.size());

            List<ApplicantDocument> documents = new ArrayList<>();
            int successCount = 0;
            int errorCount = 0;

            for (String id : applicantIds) {
                try {
                    ApplicantDocument document = fetchApplicantDataWithSQL(id);
                    if (document != null) {
                        documents.add(document);
                        successCount++;

                        // Save in batches of 100
                        if (documents.size() >= 100) {
                            elasticsearchRepository.saveAll(documents);
                            log.info("Saved batch of {} applicants", documents.size());
                            documents.clear();
                        }
                    }
                } catch (Exception e) {
                    errorCount++;
                    log.error("Error syncing applicant {}: {}", id, e.getMessage());
                }
            }

            // Save remaining documents
            if (!documents.isEmpty()) {
                elasticsearchRepository.saveAll(documents);
                log.info("Saved final batch of {} applicants", documents.size());
            }

            log.info("Synced {} applicants successfully, {} errors", successCount, errorCount);
        } catch (Exception e) {
            log.error("Error during sync all: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to sync all applicants", e);
        }
    }

    @Override
    public void deleteAllAndResync() {
        log.info("Deleting all applicants from Elasticsearch and resyncing");
        try {
            elasticsearchRepository.deleteAll();
            log.info("All applicants deleted from Elasticsearch");

            Thread.sleep(2000); // Wait for deletion to complete

            syncAllApplicantsFromDatabase();
            log.info("All applicants resynced successfully");
        } catch (Exception e) {
            log.error("Error during delete and resync: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete and resync", e);
        }
    }

    @SuppressWarnings("unchecked")
    private ApplicantDocument fetchApplicantDataWithSQL(String applicantId) {
        try {
            entityManager.setFlushMode(FlushModeType.COMMIT);

            // Fetch applicant basic data
            String sql = """
                SELECT DISTINCT 
                    u.id, u.first_name, u.last_name, u.email, u.phone, u.address, 
                    u.birthday, u.gender, u.user_status, u.create_at, u.update_at,
                    a.years_of_experience, a.career_objective, a.university_name, 
                    a.gpa, a.major
                FROM tbl_users u
                INNER JOIN tbl_applicant a ON u.id = a.user_id
                WHERE u.id = ?1
                LIMIT 1
                """;

            List<Object[]> results = entityManager.createNativeQuery(sql)
                    .setParameter(1, applicantId)
                    .getResultList();

            if (results.isEmpty()) {
                log.warn("Applicant not found: {}", applicantId);
                return null;
            }

            Object[] row = results.get(0);
            String id = (String) row[0];
            String firstName = (String) row[1];
            String lastName = (String) row[2];
            String email = (String) row[3];
            String phone = (String) row[4];
            String address = (String) row[5];
            Date birthday = (Date) row[6];
            String genderStr = (String) row[7];
            String statusStr = (String) row[8];
            Date createAt = (Date) row[9];
            Date updateAt = (Date) row[10];
            Integer yearsOfExperience = (Integer) row[11];
            String careerObjective = (String) row[12];
            String universityName = (String) row[13];
            BigDecimal gpa = row[14] != null ? new BigDecimal(row[14].toString()) : null;
            String major = (String) row[15];

            // Fetch skills separately
            List<String> skills = entityManager.createNativeQuery(
                    "SELECT DISTINCT skill FROM tbl_applicant_skill WHERE applicant_id = ?1"
            ).setParameter(1, applicantId).getResultList();

            // Fetch certificates separately
            List<Object[]> certResults = entityManager.createNativeQuery(
                    "SELECT DISTINCT certificate_name, issuing_organization FROM tbl_applicant_certificate WHERE applicant_id = ?1"
            ).setParameter(1, applicantId).getResultList();

            Set<String> certificateNames = new HashSet<>();
            Set<String> issuingOrgs = new HashSet<>();
            for (Object[] cert : certResults) {
                if (cert[0] != null) certificateNames.add((String) cert[0]);
                if (cert[1] != null) issuingOrgs.add((String) cert[1]);
            }

            // Build searchable text
            StringBuilder searchableText = new StringBuilder();
            if (firstName != null) searchableText.append(firstName).append(" ");
            if (lastName != null) searchableText.append(lastName).append(" ");
            if (email != null) searchableText.append(email).append(" ");
            if (address != null) searchableText.append(address).append(" ");
            if (phone != null) searchableText.append(phone).append(" ");
            if (careerObjective != null) searchableText.append(careerObjective).append(" ");
            if (universityName != null) searchableText.append(universityName).append(" ");
            if (major != null) searchableText.append(major).append(" ");
            if (skills != null && !skills.isEmpty()) {
                searchableText.append(String.join(" ", skills)).append(" ");
            }
            if (!certificateNames.isEmpty()) {
                searchableText.append(String.join(" ", certificateNames)).append(" ");
            }
            if (!issuingOrgs.isEmpty()) {
                searchableText.append(String.join(" ", issuingOrgs)).append(" ");
            }

            return ApplicantDocument.builder()
                    .id(id)
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .userName(email) // Using email as username
                    .address(address)
                    .birthday(birthday != null ? birthday.toLocalDate() : null)
                    .phone(phone)
                    .gender(genderStr != null ? Gender.valueOf(genderStr) : null)
                    .userStatus(statusStr != null ? UserStatus.valueOf(statusStr) : null)
                    .createAt(createAt != null ? createAt.toLocalDate() : null)
                    .updateAt(updateAt != null ? updateAt.toLocalDate() : null)
                    .yearsOfExperience(yearsOfExperience)
                    .careerObjective(careerObjective)
                    .universityName(universityName)
                    .gpa(gpa != null ? gpa.doubleValue() : null)
                    .major(major)
                    .skills(new HashSet<>(skills))
                    .certificateNames(certificateNames)
                    .issuingOrganizations(issuingOrgs)
                    .searchableText(searchableText.toString().trim())
                    .build();

        } catch (Exception e) {
            log.error("Error fetching applicant data with SQL for {}: {}", applicantId, e.getMessage(), e);
            return null;
        } finally {
            entityManager.setFlushMode(FlushModeType.AUTO);
        }
    }
}
