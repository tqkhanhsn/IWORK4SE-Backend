package vn.iwork4se.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.iwork4se.common.Gender;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedApplicantResponse {
    private String id;
    private String listId;
    private String listName;
    private String applicantId;
    private String applicantName;
    private String applicantEmail;
    private String applicantPhone;
    private Gender applicantGender;
    private Integer yearsOfExperience;
    private String major;
    private Double gpa;
    private Boolean isContacted;
    private LocalDate savedDate;
    private String notes;
}
