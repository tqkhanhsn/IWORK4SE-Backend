package vn.iwork4se.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class CVUpdateRequest implements Serializable {
    @NotBlank(message = "CV ID must not be blank")
    private String id;

    private String url; // Optional - update URL
}
