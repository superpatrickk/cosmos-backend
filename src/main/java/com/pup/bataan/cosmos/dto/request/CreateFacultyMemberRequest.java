package com.pup.bataan.cosmos.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFacultyMemberRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private List<String> programs;

    // Kept for older clients; new clients should submit programs.
    private String department;

    @Email(message = "Please provide a valid email address")
    private String email;

    private String phone;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    private String status;
}
