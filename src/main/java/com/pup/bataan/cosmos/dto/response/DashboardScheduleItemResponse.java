package com.pup.bataan.cosmos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardScheduleItemResponse {
    private Long id;
    private String code;
    private Integer students;
    private String startTime;
    private String endTime;
    private String room;
}
