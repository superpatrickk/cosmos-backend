package com.pup.bataan.cosmos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subjects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false)
    @Builder.Default
    private Integer units = 3;

    @Column(nullable = false, length = 40)
    private String type;

    @Column(nullable = false, length = 120)
    private String prerequisite;

    @Column(nullable = false, length = 20)
    private String course;

    @Column(nullable = false, length = 20)
    private String yearLevel;

    @Column(nullable = false, length = 30)
    private String semester;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "Active";

    @PrePersist
    @PreUpdate
    private void normalize() {
        if (code != null) code = code.trim().toUpperCase();
        if (type != null) type = type.trim();
        if (prerequisite != null) prerequisite = prerequisite.trim();
        if (course != null) course = course.trim().toUpperCase();
        if (yearLevel != null) yearLevel = yearLevel.trim();
        if (semester != null) semester = semester.trim();
        if (description != null) description = description.trim();
        if (status == null || status.isBlank()) status = "Active";
        if (units == null) units = 3;
    }
}
