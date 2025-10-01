package vn.iwork4se.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class PageResponseAbstract implements Serializable {
    public int pageNumber;
    public int pageSize;
    public int totalPages;
    public long totalElements;
}
