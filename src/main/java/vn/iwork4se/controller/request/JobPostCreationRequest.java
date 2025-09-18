package vn.iwork4se.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.io.Serializable;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import java.io.Serializable;
@Getter
public class JobPostCreationRequest implements Serializable {
        @NotBlank(message = "title must not be blank")
        private String title;

        @NotBlank(message = "description must not be blank")
        @Size(min = 20, max = 2000, message = "description length must be between 20 and 2000 characters")
        private String description;

        @NotBlank(message = "job position must not be blank")
        private String jobPosition;

        @NotBlank(message = "location must not be blank")
        private String location;

        @NotBlank(message = "experience must not be blank")
        private String experience;

        @PositiveOrZero(message = "min salary must be >= 0")
        private Double minSalary;

        @PositiveOrZero(message = "max salary must be >= 0")
        private Double maxSalary;

        @Min(value = 1, message = "vacancies must be at least 1")
        private Integer vacancies;

        @NotBlank(message = "job type must not be blank")
        private String jobType;

        // employer id
        @NotBlank(message = "employerId must not be blank")
        private String employerId;
    }

