package vn.iwork4se.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class CVCreationRequest implements Serializable {
    @NotBlank(message = "URL must not be blank")
    private String url;

    @NotBlank(message = "applicant ID must not be blank")
    private String applicantId;
}
