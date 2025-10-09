package vn.iwork4se.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateContactStatusRequest {
    @NotBlank(message = "Saved applicant ID is required")
    private String savedApplicantId;

    @NotNull(message = "Contact status is required")
    private Boolean isContacted;
}
