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
public class CreateSubjectRequest {

    @NotBlank(message = "Subject code is required")
    private String code;

    @Min(value = 1, message = "Units must be at least 1")
    private Integer units;

    @NotBlank(message = "Subject type is required")
    private String type;

    @NotBlank(message = "Prerequisite is required")
    private String prerequisite;

    @NotBlank(message = "Course is required")
    private String course;

    @NotBlank(message = "Year level is required")
    private String yearLevel;

    @NotBlank(message = "Semester is required")
    private String semester;

    @NotBlank(message = "Description is required")
    private String description;

    private String status;
}
