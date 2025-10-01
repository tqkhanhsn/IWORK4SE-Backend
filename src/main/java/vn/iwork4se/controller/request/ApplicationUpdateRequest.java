package vn.iwork4se.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import vn.iwork4se.common.ApplicationStatus;

import java.io.Serializable;

@Getter
public class ApplicationUpdateRequest implements Serializable {
    @NotBlank(message = "application ID must not be blank")
    private String id;

    private String cvId; // Optional - update CV

    private ApplicationStatus applicationStatus; // Optional - update status
}
