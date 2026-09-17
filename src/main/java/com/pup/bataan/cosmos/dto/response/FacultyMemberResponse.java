package com.pup.bataan.cosmos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacultyMemberResponse {
    private Long id;
    private String name;
    private String department;
    private String email;
    private String phone;
    private String specialization;
    private String status;
}
