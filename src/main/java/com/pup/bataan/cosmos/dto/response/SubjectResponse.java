package com.pup.bataan.cosmos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectResponse {

    private Long id;
    private String code;
    private Integer units;
    private String type;
    private String prerequisite;
    private String course;
    private String yearLevel;
    private String semester;
    private String description;
    private String status;
}
