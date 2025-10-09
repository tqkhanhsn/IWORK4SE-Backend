package vn.iwork4se.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import vn.iwork4se.elasticsearch.listener.SavedApplicantIndexListener;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_saved_applicant")
@EntityListeners(SavedApplicantIndexListener.class)
public class SavedApplicant {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "list_id", nullable = false)
    private SavedApplicantList savedApplicantList;

    @ManyToOne
    @JoinColumn(name = "applicant_id", nullable = false)
    private Applicant applicant;

    @Column(name = "is_contacted", nullable = false)
    private Boolean isContacted = false;

    @CreationTimestamp
    @Column(name = "saved_date")
    private LocalDate savedDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
