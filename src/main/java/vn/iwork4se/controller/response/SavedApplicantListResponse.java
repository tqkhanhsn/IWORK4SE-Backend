package vn.iwork4se.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedApplicantListResponse {
    private String id;
    private String employerId;
    private String listName;
    private Boolean isDefault;
    private LocalDate createdDate;
    private Long applicantCount;
    private Long contactedCount;
    private Long notContactedCount;
}
