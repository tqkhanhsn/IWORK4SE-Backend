package vn.iwork4se.controller.response;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobCategoryCreationResponse implements Serializable {
    private Long id;
    private String categoryName;
    private String description;
    private LocalDateTime createAt;
}
