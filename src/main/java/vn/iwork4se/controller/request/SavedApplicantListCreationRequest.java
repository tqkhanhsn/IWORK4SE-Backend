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
public class SavedApplicantListCreationRequest {
    @NotBlank(message = "Employer ID is required")
    private String employerId;

    @NotBlank(message = "List name is required")
    private String listName;
}
