package com.pup.bataan.cosmos.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCourseRequest {
    private String code;
    private String name;
    private String department;
    private String duration;
    private Integer students;
    private String status;
    private String description;
    private Integer yearEstablished;
    private Integer totalSections;
}
