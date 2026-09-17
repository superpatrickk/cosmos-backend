package com.pup.bataan.cosmos.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourseRequest {

    @NotBlank(message = "Course code is required")
    private String code;

    @NotBlank(message = "Course name is required")
    private String name;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Duration is required")
    private String duration;

    @Min(value = 0, message = "Students cannot be negative")
    private Integer students;

    private String status;

    @NotBlank(message = "Description is required")
    private String description;

    @Min(value = 1900, message = "Year established must be valid")
    private Integer yearEstablished;

    @Min(value = 0, message = "Total sections cannot be negative")
    private Integer totalSections;
}
