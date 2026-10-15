package com.pup.bataan.cosmos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Entity
@Table(name = "faculty_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacultyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(unique = true, length = 150)
    private String email;

    @Column(length = 20)
    private String phone;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "faculty_member_programs", joinColumns = @JoinColumn(name = "faculty_member_id"))
    @Column(name = "program_code", nullable = false, length = 20)
    @Builder.Default
    private List<String> programs = new ArrayList<>();

    @Column(nullable = false, length = 100)
    private String specialization;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private FacultyStatus status = FacultyStatus.ACTIVE;

    @PrePersist
    @PreUpdate
    private void normalizeFields() {
        if (name != null) {
            name = name.trim();
        }
        if (programs == null) {
            programs = new ArrayList<>();
        }
        programs = programs.stream()
                .filter(program -> program != null && !program.isBlank())
                .map(program -> program.trim().toUpperCase(Locale.ROOT))
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
        if (department != null) {
            department = department.trim();
        }
        if (email != null) {
            email = email.trim().toLowerCase();
        }
        if (phone != null) {
            phone = phone.trim();
        }
        if (specialization != null) {
            specialization = specialization.trim();
        }
        if (status == null) {
            status = FacultyStatus.ACTIVE;
        }
    }
}
