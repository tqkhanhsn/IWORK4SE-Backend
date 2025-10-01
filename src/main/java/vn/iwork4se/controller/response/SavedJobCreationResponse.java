package vn.iwork4se.controller.response;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedJobCreationResponse implements Serializable {
    private String id;
    private String applicantId;
    private String jobId;
    private LocalDate savedDate;
}
