package com.pup.bataan.cosmos.repository;

import com.pup.bataan.cosmos.entity.ScheduleEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntry, Long> {
    List<ScheduleEntry> findAllByOrderByDayAsc();

    List<ScheduleEntry> findByFacultyNameContainingIgnoreCase(String facultyName);

    List<ScheduleEntry> findByDayContainingIgnoreCaseOrSubjectNameContainingIgnoreCaseOrFacultyNameContainingIgnoreCase(
            String day,
            String subjectName,
            String facultyName
    );
}
