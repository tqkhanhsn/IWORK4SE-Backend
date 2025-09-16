package vn.iwork4se.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_employer")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "user_id")
public class Employer extends User{
    @Column(name = "company_name")
    private String companyName;

    private String location;

    private String industry;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo_url")
    private String logoUrl;

    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL)
    private Set<JobPost> jobPosts = new HashSet<>();
}
