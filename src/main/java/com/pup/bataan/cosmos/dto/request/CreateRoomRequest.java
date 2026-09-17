package com.pup.bataan.cosmos.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomRequest {

    @NotBlank(message = "Room name is required")
    private String name;

    @NotBlank(message = "Room code is required")
    private String code;

    @NotBlank(message = "Building is required")
    private String building;

    @NotBlank(message = "Floor is required")
    private String floor;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    // type removed — DB no longer contains this column

    private String status;
    // description and amenities removed — DB no longer contains these columns
}
