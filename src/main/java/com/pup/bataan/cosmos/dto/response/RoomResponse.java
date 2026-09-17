package com.pup.bataan.cosmos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private Long id;
    private String code;
    private String name;
    private String building;
    private String floor;
    private Integer capacity;
    // type removed — DB no longer contains this column
    private String status;
    // description and amenities removed — DB no longer contains these columns
}
