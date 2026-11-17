package com.pup.bataan.cosmos.dto.request;

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
