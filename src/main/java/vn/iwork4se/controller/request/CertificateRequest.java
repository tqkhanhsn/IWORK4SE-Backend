package vn.iwork4se.controller.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@ToString
public class CertificateRequest implements Serializable {
    @NotBlank(message = "Certificate name is mandatory")
    private String certificateName;
    @NotBlank(message = "Issuing organization is mandatory")
    private String issuingOrganization;
    @NotBlank(message = "Issue date is mandatory")
    private LocalDate issueDate;
    @NotBlank(message = "Expiration date is mandatory")
    private LocalDate expirationDate;
    @NotBlank(message = "Certificate ID is mandatory")
    private String certificateId;
    @NotBlank(message = "Certificate URL is mandatory")
    private String certificateUrl;
    private String notes;

}
