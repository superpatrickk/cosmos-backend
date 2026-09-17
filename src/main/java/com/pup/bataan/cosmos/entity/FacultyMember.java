package com.pup.bataan.cosmos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

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
