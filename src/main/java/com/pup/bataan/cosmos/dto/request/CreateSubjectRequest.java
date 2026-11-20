package com.pup.bataan.cosmos.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubjectRequest {

    @NotBlank(message = "Subject code is required")
    private String code;

    private String name;

    @Min(value = 1, message = "Units must be at least 1")
    @Max(value = 15, message = "Units cannot exceed 15")
    private Integer units;

    @Min(value = 0, message = "Lecture hours cannot be negative")
    private Integer lec;

    @Min(value = 0, message = "Laboratory hours cannot be negative")
    private Integer lab;

    @NotBlank(message = "Subject type is required")
    private String type;

    @NotBlank(message = "Prerequisite is required")
    private String prerequisite;

    @NotBlank(message = "Program is required")
    private String course;

    @NotBlank(message = "Year level is required")
    private String yearLevel;

    @NotBlank(message = "Curriculum is required")
    private String academicYear;

    @NotBlank(message = "Semester is required")
    private String semester;

    @NotBlank(message = "Description is required")
    private String description;

    private String status;
}
