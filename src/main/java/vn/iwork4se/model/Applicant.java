package vn.iwork4se.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_applicant")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "user_id")
public class Applicant extends User {
    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(name = "career_objective", columnDefinition = "TEXT")
    private String careerObjective;

    @Column(name = "university_name")
    private String universityName;

    private Double gpa;

    private String major;

    @ElementCollection
    @CollectionTable(name = "tbl_applicant_skill", joinColumns = @JoinColumn(name = "applicant_id"))
    @Column(name = "skill")
    private Set<String> skills = new HashSet<>();

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Certificate> certificates = new HashSet<>();

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL)
    private Set<Application> applications = new HashSet<>();

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL)
    private Set<SavedJob> savedJobs = new HashSet<>();

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL)
    private Set<CV> cvs = new HashSet<>();
}
