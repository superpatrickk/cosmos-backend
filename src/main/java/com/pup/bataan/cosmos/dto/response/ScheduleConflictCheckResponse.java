package com.pup.bataan.cosmos.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ScheduleConflictCheckResponse {
    private boolean hasConflict;
    private List<ScheduleConflictResponse> conflicts;
}
