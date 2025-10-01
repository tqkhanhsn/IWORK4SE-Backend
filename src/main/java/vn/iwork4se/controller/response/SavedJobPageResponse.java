package vn.iwork4se.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SavedJobPageResponse extends PageResponseAbstract {
    private List<SavedJobResponse> content;

    public SavedJobPageResponse(List<SavedJobResponse> content, int pageNumber, int pageSize, int totalPages, long totalElements) {
        super(pageNumber, pageSize, totalPages, totalElements);
        this.content = content;
    }
}
