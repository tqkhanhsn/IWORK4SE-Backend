package vn.iwork4se.controller.response;


import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificateResponse implements Serializable {
    private String certificateName;
    private String issuingOrganization;
    private LocalDate issueDate;
    private LocalDate expirationDate;
    private String certificateId;
    private String certificateUrl;
    private String notes;
}
