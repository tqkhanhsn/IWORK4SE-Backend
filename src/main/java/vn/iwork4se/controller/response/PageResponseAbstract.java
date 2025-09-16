package vn.iwork4se.controller.response;

import java.io.Serializable;

public abstract class PageResponseAbstract implements Serializable {
    public int pageNumber;
    public int pageSize;
    public int totalPages;
    public long totalElements;
}
