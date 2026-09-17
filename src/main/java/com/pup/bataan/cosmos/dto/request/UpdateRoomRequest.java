package com.pup.bataan.cosmos.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoomRequest {
    private String name;
    private String code;
    private String building;
    private String floor;
    private Integer capacity;
    // type removed — DB no longer contains this column
    private String status;
    // description and amenities removed — DB no longer contains these columns
}
