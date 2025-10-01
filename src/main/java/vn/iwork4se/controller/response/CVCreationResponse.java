package vn.iwork4se.controller.response;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CVCreationResponse implements Serializable {
    private String id;
    private String url;
    private LocalDate uploadedDate;
    private String applicantId;
}
