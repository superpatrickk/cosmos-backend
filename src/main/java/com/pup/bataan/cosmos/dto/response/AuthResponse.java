package com.pup.bataan.cosmos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String username;
    private String fullName;
    private String email;
    private String role;

    // Refresh token is NOT included here
    // It is sent as an HttpOnly cookie by the backend
}