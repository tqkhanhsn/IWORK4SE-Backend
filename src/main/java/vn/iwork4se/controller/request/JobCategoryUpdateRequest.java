package vn.iwork4se.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class JobCategoryUpdateRequest implements Serializable {
    @NotNull(message = "ID must not be null")
    private Long id;

    @NotBlank(message = "Category name must not be blank")
    @Size(min = 2, max = 100, message = "Category name length must be between 2 and 100 characters")
    private String categoryName;

    @Size(max = 500, message = "Description length must not exceed 500 characters")
    private String description;
}
