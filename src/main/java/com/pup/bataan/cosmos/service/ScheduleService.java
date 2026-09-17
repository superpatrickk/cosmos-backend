package com.pup.bataan.cosmos.service;

import com.pup.bataan.cosmos.dto.request.CreateScheduleRequest;
import com.pup.bataan.cosmos.dto.request.UpdateScheduleRequest;
import com.pup.bataan.cosmos.entity.ScheduleEntry;
import com.pup.bataan.cosmos.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public List<ScheduleEntry> getAll(String search) {
        if (search == null || search.isBlank()) {
            return scheduleRepository.findAllByOrderByDayAsc();
        }
        String q = search.trim();
        return scheduleRepository.findByDayContainingIgnoreCaseOrSubjectNameContainingIgnoreCaseOrFacultyNameContainingIgnoreCase(q, q, q);
    }

    public ScheduleEntry getById(Long id) {
        return scheduleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Schedule not found"));
    }

    @Transactional
    public ScheduleEntry create(CreateScheduleRequest request) {
        ScheduleEntry entry = ScheduleEntry.builder()
                .subjectCode(request.getSubjectCode())
                .subjectName(request.getSubjectName())
                .facultyName(request.getFacultyName())
                .roomId(request.getRoomId())
                .roomName(request.getRoomName())
                .day(request.getDay())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .courseCode(request.getCourseCode())
                .yearLevel(request.getYearLevel())
                .section(request.getSection())
                .semester(request.getSemester())
                .academicYear(request.getAcademicYear())
                .status(request.getStatus() == null || request.getStatus().isBlank() ? "Active" : request.getStatus())
                .build();
        return scheduleRepository.save(entry);
    }

    @Transactional
    public ScheduleEntry update(Long id, UpdateScheduleRequest request) {
        ScheduleEntry entry = getById(id);
        if (request.getSubjectCode() != null) entry.setSubjectCode(request.getSubjectCode());
        if (request.getSubjectName() != null) entry.setSubjectName(request.getSubjectName());
        if (request.getFacultyName() != null) entry.setFacultyName(request.getFacultyName());
        if (request.getRoomName() != null) entry.setRoomName(request.getRoomName());
        if (request.getRoomId() != null) entry.setRoomId(request.getRoomId());
        if (request.getDay() != null) entry.setDay(request.getDay());
        if (request.getStartTime() != null) entry.setStartTime(request.getStartTime());
        if (request.getEndTime() != null) entry.setEndTime(request.getEndTime());
        if (request.getCourseCode() != null) entry.setCourseCode(request.getCourseCode());
        if (request.getYearLevel() != null) entry.setYearLevel(request.getYearLevel());
        if (request.getSection() != null) entry.setSection(request.getSection());
        if (request.getSemester() != null) entry.setSemester(request.getSemester());
        if (request.getAcademicYear() != null) entry.setAcademicYear(request.getAcademicYear());
        if (request.getStatus() != null) entry.setStatus(request.getStatus());
        return scheduleRepository.save(entry);
    }

    @Transactional
    public void delete(Long id) {
        scheduleRepository.deleteById(id);
    }
}
