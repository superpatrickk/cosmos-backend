package com.pup.bataan.cosmos.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScheduleConflictResponse {
    private Long id;
    private String subjectCode;
    private String faculty;
    private String room;
    private String day;
    private String startTime;
    private String endTime;
}
