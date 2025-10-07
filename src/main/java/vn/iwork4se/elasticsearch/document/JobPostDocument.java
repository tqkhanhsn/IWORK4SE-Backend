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
import vn.iwork4se.common.JobStatus;
import vn.iwork4se.common.JobType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(indexName = "job_posts")
@Setting(settingPath = "elasticsearch-settings.json")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String jobPosition;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String location;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String experience;

    @Field(type = FieldType.Double)
    private Double minSalary;

    @Field(type = FieldType.Double)
    private Double maxSalary;

    @Field(type = FieldType.Date)
    private LocalDate postedDate;

    @Field(type = FieldType.Date)
    private LocalDate closingDate;

    @Field(type = FieldType.Integer)
    private Integer vacancies;

    @Field(type = FieldType.Keyword)
    private JobStatus jobStatus;

    @Field(type = FieldType.Keyword)
    private JobType jobType;

    @Field(type = FieldType.Date)
    private LocalDate updateAt;

    // Employer information
    @Field(type = FieldType.Keyword)
    private String employerId;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String employerName;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String companyName;

    @Field(type = FieldType.Keyword)
    private String logoUrl; // Added logoUrl field from employer

    // Category information
    @Field(type = FieldType.Long)
    private Long categoryId;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String categoryName;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String searchableText;
}
