package vn.iwork4se.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import vn.iwork4se.common.ApplicationStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_application")
@Data
@EqualsAndHashCode(exclude = {"job", "applicant", "cv"})
@ToString(exclude = {"job", "applicant", "cv"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private JobPost job;

    @ManyToOne
    @JoinColumn(name = "applicant_id", nullable = false)
    private Applicant applicant;

    @CreationTimestamp
    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_status")
    private ApplicationStatus applicationStatus;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @OneToOne
    @JoinColumn(name = "cv_id")
    private CV cv;
}
