package vn.iwork4se.controller.response;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedJobResponse implements Serializable {
    private String id;
    private String applicantId;
    private String applicantName;
    private String jobId;
    private String jobTitle;
    private String jobPosition;
    private String jobLocation;
    private String companyName;
    private LocalDate savedDate;
}
