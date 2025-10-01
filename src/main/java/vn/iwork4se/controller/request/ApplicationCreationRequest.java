package vn.iwork4se.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class ApplicationCreationRequest implements Serializable {
    @NotBlank(message = "applicant ID must not be blank")
    private String applicantId;

    @NotBlank(message = "job ID must not be blank")
    private String jobId;

    private String cvId; // Optional - CV to attach to application
}
