package vn.iwork4se.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedApplicantPageResponse {
    private List<SavedApplicantResponse> savedApplicants;
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalElements;
}
