package com.pup.bataan.cosmos.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateScheduleRequest {

    @NotBlank(message = "Subject code is required")
    private String subjectCode;

    @NotBlank(message = "Subject name is required")
    private String subjectName;

    @NotBlank(message = "Faculty is required")
    private String facultyName;

    @NotBlank(message = "Room is required")
    private String roomName;

    private Long roomId;

    @NotBlank(message = "Day is required")
    private String day;

    @NotBlank(message = "Start time is required")
    private String startTime;

    @NotBlank(message = "End time is required")
    private String endTime;

    @NotBlank(message = "Course is required")
    private String courseCode;

    @NotBlank(message = "Year level is required")
    private String yearLevel;

    @NotBlank(message = "Section is required")
    private String section;

    @NotBlank(message = "Semester is required")
    private String semester;

    @NotBlank(message = "Academic year is required")
    private String academicYear;

    private String status;
}
