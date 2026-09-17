package com.pup.bataan.cosmos.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateScheduleRequest {
    private String subjectCode;
    private String subjectName;
    private String facultyName;
    private String roomName;
    private Long roomId;
    private String day;
    private String startTime;
    private String endTime;
    private String courseCode;
    private String yearLevel;
    private String section;
    private String semester;
    private String academicYear;
    private String status;
}
