package vn.iwork4se.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @OneToOne(mappedBy = "cv")
    private Application application;
}
