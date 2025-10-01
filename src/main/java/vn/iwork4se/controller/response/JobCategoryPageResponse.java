package vn.iwork4se.controller.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JobCategoryPageResponse extends PageResponseAbstract {
    private List<JobCategoryResponse> content;
    
    public JobCategoryPageResponse(List<JobCategoryResponse> content, int pageNumber, int pageSize, int totalPages, long totalElements) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
