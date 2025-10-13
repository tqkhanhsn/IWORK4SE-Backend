package vn.iwork4se.controller.response;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CVResponse implements Serializable {
    private String id;
    private String url;
    private LocalDateTime uploadedDate;
    private String applicantId;
    private String applicantName;
    private boolean isUsedInApplication;
    private String applicationId;
}
