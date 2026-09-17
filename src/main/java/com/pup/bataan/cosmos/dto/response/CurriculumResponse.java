package com.pup.bataan.cosmos.dto.response;

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
public class CurriculumResponse {

    private Long id;

    private String program;

    private String yearLevel;

    private String semester;

    private String academicYear;

    private String status;

    private String description;

    private List<Map<String, Object>> subjects;

    private Integer totalSubjects;

    private Integer totalUnits;
}
