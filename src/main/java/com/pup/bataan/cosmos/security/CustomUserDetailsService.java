package com.pup.bataan.cosmos.security;

import com.pup.bataan.cosmos.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return adminRepository.findByUsername(username)
                .orElseThrow(() ->
                    new UsernameNotFoundException(
                        "User not found: " + username
                    )
                );
    }
}