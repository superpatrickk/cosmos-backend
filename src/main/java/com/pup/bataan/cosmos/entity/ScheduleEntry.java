package com.pup.bataan.cosmos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String subjectCode;

    @Column(nullable = false, length = 120)
    private String subjectName;

    @Column(nullable = false, length = 120)
    private String facultyName;

    @Column(nullable = false)
    private Long roomId;

    @Column(nullable = false, length = 120)
    private String roomName;

    @Column(nullable = false, length = 20)
    private String day;

    @Column(nullable = false, length = 10)
    private String startTime;

    @Column(nullable = false, length = 10)
    private String endTime;

    @Column(nullable = false, length = 20)
    private String courseCode;

    @Column(nullable = false, length = 30)
    private String yearLevel;

    @Column(nullable = false, length = 20)
    private String section;

    @Column(nullable = false, length = 40)
    private String semester;

    @Column(nullable = false, length = 20)
    private String academicYear;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "Active";

    @PrePersist
    @PreUpdate
    private void normalize() {
        if (subjectCode != null) subjectCode = subjectCode.trim().toUpperCase();
        if (subjectName != null) subjectName = subjectName.trim();
        if (facultyName != null) facultyName = facultyName.trim();
        if (roomName != null) roomName = roomName.trim();
        if (day != null) day = day.trim();
        if (courseCode != null) courseCode = courseCode.trim().toUpperCase();
        if (yearLevel != null) yearLevel = yearLevel.trim();
        if (section != null) section = section.trim().toUpperCase();
        if (semester != null) semester = semester.trim();
        if (academicYear != null) academicYear = academicYear.trim();
        if (status == null || status.isBlank()) status = "Active";
    }
}
