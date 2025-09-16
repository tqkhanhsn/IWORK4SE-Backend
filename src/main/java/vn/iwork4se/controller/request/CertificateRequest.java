package vn.iwork4se.controller.request;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@ToString
public class CertificateRequest implements Serializable {
    private String certificateName;
    private String issuingOrganization;
    private LocalDate issueDate;
    private LocalDate expirationDate;
    private String certificateId;
    private String certificateUrl;
    private String notes;

}
