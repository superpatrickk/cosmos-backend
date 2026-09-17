package com.pup.bataan.cosmos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomMetadataResponse {
    private List<String> buildings;
    private List<String> floors;
    private List<String> types;
    private List<String> statuses;
    // amenities removed — DB no longer contains this column
}
