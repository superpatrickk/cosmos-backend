package com.pup.bataan.cosmos.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFacultyMemberRequest {
    private String name;
    private String department;

    @Email(message = "Please provide a valid email address")
    private String email;

    private String phone;
    private String specialization;
    private String status;
}
