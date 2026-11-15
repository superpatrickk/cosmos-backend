package com.pup.bataan.cosmos.service;

import com.pup.bataan.cosmos.dto.request.CreateScheduleRequest;
import com.pup.bataan.cosmos.dto.request.UpdateScheduleRequest;
import com.pup.bataan.cosmos.dto.response.ScheduleConflictCheckResponse;
import com.pup.bataan.cosmos.dto.response.ScheduleConflictResponse;
import com.pup.bataan.cosmos.entity.ScheduleEntry;
import com.pup.bataan.cosmos.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

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

    public ScheduleConflictCheckResponse checkConflicts(CreateScheduleRequest request) {
        LocalTime requestedStart = LocalTime.parse(request.getStartTime());
        LocalTime requestedEnd = LocalTime.parse(request.getEndTime());

        List<ScheduleConflictResponse> conflicts = scheduleRepository.findAll().stream()
                .filter(schedule -> "active".equalsIgnoreCase(schedule.getStatus()))
                .filter(schedule -> sameDay(schedule.getDay(), request.getDay()))
                .filter(schedule -> overlaps(
                        requestedStart,
                        requestedEnd,
                        LocalTime.parse(schedule.getStartTime()),
                        LocalTime.parse(schedule.getEndTime())
                ))
                .filter(schedule -> conflictsWith(schedule, request))
                .map(schedule -> ScheduleConflictResponse.builder()
                        .id(schedule.getId())
                        .subjectCode(schedule.getSubjectCode())
                        .faculty(schedule.getFacultyName())
                        .room(schedule.getRoomName())
                        .day(schedule.getDay())
                        .startTime(schedule.getStartTime())
                        .endTime(schedule.getEndTime())
                        .build())
                .toList();

        return ScheduleConflictCheckResponse.builder()
                .hasConflict(!conflicts.isEmpty())
                .conflicts(conflicts)
                .build();
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

    private boolean conflictsWith(ScheduleEntry schedule, CreateScheduleRequest request) {
        return sameValue(schedule.getFacultyName(), request.getFacultyName())
                || (request.getRoomId() != null && request.getRoomId().equals(schedule.getRoomId()))
                || sameValue(schedule.getRoomName(), request.getRoomName())
                || (sameValue(schedule.getCourseCode(), request.getCourseCode())
                && sameValue(schedule.getSection(), request.getSection())
                && sameValue(schedule.getYearLevel(), request.getYearLevel())
                && sameValue(schedule.getSemester(), request.getSemester())
                && sameValue(schedule.getAcademicYear(), request.getAcademicYear()));
    }

    private boolean sameDay(String first, String second) {
        return first != null && second != null && first.trim().equalsIgnoreCase(second.trim());
    }

    private boolean sameValue(String first, String second) {
        return first != null && second != null
                && first.trim().toLowerCase(Locale.ROOT).equals(second.trim().toLowerCase(Locale.ROOT));
    }

    private boolean overlaps(LocalTime firstStart, LocalTime firstEnd, LocalTime secondStart, LocalTime secondEnd) {
        return firstStart.isBefore(secondEnd) && secondStart.isBefore(firstEnd);
    }
}
