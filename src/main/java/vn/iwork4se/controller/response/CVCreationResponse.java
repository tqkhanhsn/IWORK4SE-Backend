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
public class CVCreationResponse implements Serializable {
    private String id;
    private String url;
    private LocalDateTime uploadedDate;
    private String applicantId;
}
