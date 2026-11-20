package com.pup.bataan.cosmos.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSubjectRequest {

    private String code;
    private String name;
    @Min(value = 1, message = "Units must be at least 1")
    @Max(value = 15, message = "Units cannot exceed 15")
    private Integer units;
    private Integer lec;
    private Integer lab;
    private String type;
    private String prerequisite;
    private String course;
    private String yearLevel;
    private String academicYear;
    private String semester;
    private String description;
    private String status;
}
