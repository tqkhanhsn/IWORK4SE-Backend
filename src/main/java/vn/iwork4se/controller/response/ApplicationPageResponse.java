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
public class ApplicationPageResponse extends PageResponseAbstract {
    private List<ApplicationResponse> content;

    public ApplicationPageResponse(List<ApplicationResponse> content, int pageNumber, int pageSize, int totalPages, long totalElements) {
        super(pageNumber, pageSize, totalPages, totalElements);
        this.content = content;
    }
}
