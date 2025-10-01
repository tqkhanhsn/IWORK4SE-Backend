package vn.iwork4se.controller.response;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobCategoryResponse implements Serializable {
    private Long id;
    private String categoryName;
    private String description;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Long jobPostCount; // Số lượng job posts thuộc category này
}
