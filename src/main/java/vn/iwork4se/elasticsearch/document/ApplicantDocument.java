package vn.iwork4se.elasticsearch.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;
import vn.iwork4se.common.DegreeLevel;
import vn.iwork4se.common.Gender;
import vn.iwork4se.common.UserStatus;

import java.time.LocalDate;
import java.util.Set;

@Document(indexName = "applicants")
@Setting(settingPath = "elasticsearch-settings.json")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantDocument {

    @Id
    private String id;

    // User fields
    @Field(type = FieldType.Text, analyzer = "standard")
    private String firstName;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String lastName;

    @Field(type = FieldType.Keyword)
    private String email;

    @Field(type = FieldType.Keyword)
    private String userName;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String address;

    @Field(type = FieldType.Date)
    private LocalDate birthday;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String phone;

    @Field(type = FieldType.Keyword)
    private Gender gender;

    @Field(type = FieldType.Keyword)
    private UserStatus userStatus;

    @Field(type = FieldType.Date)
    private LocalDate createAt;

    @Field(type = FieldType.Date)
    private LocalDate updateAt;

    // Applicant specific fields
    @Field(type = FieldType.Integer)
    private Integer yearsOfExperience;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String careerObjective;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String universityName;

    @Field(type = FieldType.Keyword)
    private DegreeLevel degreeLevel;

    @Field(type = FieldType.Integer)
    private Integer graduationYear;

    @Field(type = FieldType.Double)
    private Double gpa;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String major;

    @Field(type = FieldType.Text, analyzer = "standard")
    private Set<String> skills;

    // Certificate information (flattened for search)
    @Field(type = FieldType.Text, analyzer = "standard")
    private Set<String> certificateNames;

    @Field(type = FieldType.Text, analyzer = "standard")
    private Set<String> issuingOrganizations;

    @Field(type = FieldType.Keyword)
    private Set<String> savedInListIds;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String searchableText;
}
