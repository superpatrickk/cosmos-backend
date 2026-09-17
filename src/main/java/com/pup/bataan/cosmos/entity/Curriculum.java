package com.pup.bataan.cosmos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "curricula")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Curriculum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String program;

    @Column(nullable = false, length = 30)
    private String yearLevel;

    @Column(nullable = false, length = 30)
    private String semester;

    @Column(nullable = false, length = 20)
    private String academicYear;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "Active";

    @Column(length = 1000)
    private String description;

    @Column(columnDefinition = "LONGTEXT")
    private String subjectsJson;

    @PrePersist
    @PreUpdate
    private void normalizeFields() {
        if (program != null) {
            program = program.trim();
        }
        if (yearLevel != null) {
            yearLevel = yearLevel.trim();
        }
        if (semester != null) {
            semester = semester.trim();
        }
        if (academicYear != null) {
            academicYear = academicYear.trim();
        }
        if (status != null) {
            status = status.trim();
        }
    }
}
