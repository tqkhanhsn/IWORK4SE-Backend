package vn.iwork4se.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    @Column(name = "uploaded_date")
    private LocalDate uploadedDate;

    @ManyToOne
    @JoinColumn(name = "applicant_id", nullable = false)
    private Applicant applicant;

    @OneToOne(mappedBy = "cv")
    private Application application;
}
