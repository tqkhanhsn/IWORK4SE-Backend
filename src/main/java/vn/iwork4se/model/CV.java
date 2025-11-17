package vn.iwork4se.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_cv")
public class CV {
    @Id
    private String id;

    @Column(nullable = false)
    private String url;

    @Column
    private String fileName;

    @CreationTimestamp
    @Column(name = "uploaded_date")
    private LocalDateTime uploadedDate;

    @ManyToOne
    @JoinColumn(name = "applicant_id", nullable = false)
    private Applicant applicant;

    @OneToMany(mappedBy = "cv", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    private List<Application> applications = new ArrayList<>();
}
