package vn.iwork4se.controller.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JobPostPageResponse extends PageResponseAbstract {
    private List<JobPostResponse> content;
    
    public JobPostPageResponse(List<JobPostResponse> content, int pageNumber, int pageSize, int totalPages, long totalElements) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
