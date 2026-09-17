package com.pup.bataan.cosmos.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCurriculumRequest {

    @NotBlank(message = "Program is required")
    private String program;

    @NotBlank(message = "Year level is required")
    private String yearLevel;

    @NotBlank(message = "Semester is required")
    private String semester;

    @NotBlank(message = "Academic year is required")
    private String academicYear;

    @Builder.Default
    private String status = "Active";

    private String description;

    @Builder.Default
    private List<Map<String, Object>> subjects = List.of();
}
