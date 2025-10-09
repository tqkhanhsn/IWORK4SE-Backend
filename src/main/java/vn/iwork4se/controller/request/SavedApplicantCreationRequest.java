package vn.iwork4se.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedApplicantCreationRequest {
    @NotBlank(message = "List ID is required")
    private String listId;

    @NotBlank(message = "Applicant ID is required")
    private String applicantId;

    private String notes;
}
