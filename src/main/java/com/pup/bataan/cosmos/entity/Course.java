package com.pup.bataan.cosmos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 120)
    private String department;

    @Column(nullable = false, length = 20)
    private String duration;

    @Column(nullable = false)
    @Builder.Default
    private Integer students = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private CourseStatus status = CourseStatus.ACTIVE;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private Integer yearEstablished;

    @Column(nullable = false)
    @Builder.Default
    private Integer totalSections = 0;

    @PrePersist
    @PreUpdate
    private void normalize() {
        if (code != null) {
            code = code.trim().toUpperCase();
        }
        if (name != null) {
            name = name.trim();
        }
        if (department != null) {
            department = department.trim();
        }
        if (duration != null) {
            duration = duration.trim();
        }
        if (description != null) {
            description = description.trim();
        }
        if (status == null) {
            status = CourseStatus.ACTIVE;
        }
    }
}
