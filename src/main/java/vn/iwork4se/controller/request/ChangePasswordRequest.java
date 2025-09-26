package vn.iwork4se.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotNull
    private String id;
    @NotBlank(message = "Password is mandatory")
    private String oldPassword;
    @NotBlank(message = "Password is mandatory")
    private String newPassword;
    @NotBlank(message = "Password is mandatory")
    private String confirmPassword;
}
