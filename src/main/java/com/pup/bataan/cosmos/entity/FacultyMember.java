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

    @Column(length = 60)
    private String type;

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
        if (type != null) {
            type = switch (type.trim()) {
                case "Core", "GEED (Full-time)", "FULL TIME FACULTY CORE" -> "Full Time Faculty Core";
                case "Allied", "FULL TIME FACULTY ALLIED" -> "Full Time Faculty Allied";
                case "FULL TIME PART TIME FACULTY CORE" -> "Full Time Faculty Core";
                case "FULL TIME PART TIME FACULTY ALLIED" -> "Full Time Faculty Allied";
                case "Part-time", "GEED (Part-time)", "PART TIME FACULTY CORE" -> "Part Time Faculty Core";
                case "PART TIME FACULTY ALLIED" -> "Part Time Faculty Allied";
                default -> type.trim();
            };
        }
        if (programs == null) {
            programs = new ArrayList<>();
        }
        List<String> normalizedPrograms = programs.stream()
                .filter(program -> program != null && !program.isBlank())
                .map(program -> program.trim().toUpperCase(Locale.ROOT))
                .distinct()
                .toList();
        programs.clear();
        programs.addAll(normalizedPrograms);
        if (department != null) {
            department = department.trim();
        }
        if (email != null) {
            email = email.trim().toLowerCase();
        }
        if (phone != null) {
            phone = phone.trim();
        } else {
            phone = "";
        }
        if (status == null) {
            status = FacultyStatus.ACTIVE;
        }
    }
}
