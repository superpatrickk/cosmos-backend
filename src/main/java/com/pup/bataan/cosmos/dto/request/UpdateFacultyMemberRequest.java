package com.pup.bataan.cosmos.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFacultyMemberRequest {
    private String name;
    private List<String> programs;
    // Kept for older clients; new clients should submit programs.
    private String department;

    @Email(message = "Please provide a valid email address")
    private String email;

    private String phone;
    private String specialization;
    private String status;
}
