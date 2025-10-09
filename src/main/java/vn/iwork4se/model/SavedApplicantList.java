package vn.iwork4se.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import vn.iwork4se.elasticsearch.listener.SavedApplicantListIndexListener;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_saved_applicant_list")
@EntityListeners(SavedApplicantListIndexListener.class)
public class SavedApplicantList {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "employer_id", nullable = false)
    private Employer employer;

    @Column(name = "list_name", nullable = false)
    private String listName;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDate createdDate;

    @OneToMany(mappedBy = "savedApplicantList", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SavedApplicant> savedApplicants = new HashSet<>();
}
