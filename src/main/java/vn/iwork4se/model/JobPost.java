package vn.iwork4se.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import vn.iwork4se.common.JobStatus;
import vn.iwork4se.common.JobType;
import vn.iwork4se.elasticsearch.listener.JobPostIndexListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_job_post")
@EntityListeners(JobPostIndexListener.class)
@Data
@EqualsAndHashCode(exclude = {"employer", "category", "applications", "savedByApplicants"})
@ToString(exclude = {"employer", "category", "applications", "savedByApplicants"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPost {
    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "job_position")
    private String jobPosition;

    private String location;

    private String experience;

    @Column(name = "min_salary")
    private Double minSalary;

    @Column(name = "max_salary")
    private Double maxSalary;

    @CreationTimestamp
    @Column(name = "posted_date")
    private LocalDate postedDate;

    @Column(name = "closing_date")
    private LocalDate closingDate;

    private Integer vacancies;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_status")
    private JobStatus jobStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type")
    private JobType jobType;


    @UpdateTimestamp
    @Column(name = "update_at")
    private LocalDateTime updateAt;


    @ManyToOne
    @JoinColumn(name = "employer_id", nullable = false)
    private Employer employer;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private JobCategory category;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private Set<Application> applications = new HashSet<>();

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private Set<SavedJob> savedByApplicants = new HashSet<>();

}
