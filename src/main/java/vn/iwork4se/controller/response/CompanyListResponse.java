package vn.iwork4se.controller.response;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyListResponse implements Serializable {
    private String companyName;
    private String industry;
    private String location;
    private String logoUrl;
    private String description;
}
