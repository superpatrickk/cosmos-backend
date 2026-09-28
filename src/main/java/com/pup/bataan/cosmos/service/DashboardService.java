package com.pup.bataan.cosmos.service;

import com.pup.bataan.cosmos.dto.response.DashboardScheduleItemResponse;
import com.pup.bataan.cosmos.dto.response.DashboardStatsResponse;
import com.pup.bataan.cosmos.entity.RoomStatus;
import com.pup.bataan.cosmos.entity.ScheduleEntry;
import com.pup.bataan.cosmos.repository.RoomRepository;
import com.pup.bataan.cosmos.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final RoomRepository roomRepository;
    private final ScheduleRepository scheduleRepository;

    public DashboardStatsResponse getStats() {
        int totalRooms = Math.toIntExact(roomRepository.count());
        int availableRooms = Math.toIntExact(roomRepository.countByStatus(RoomStatus.AVAILABLE));
        int occupiedRooms = Math.toIntExact(roomRepository.countByStatus(RoomStatus.OCCUPIED));
        int underMaintenance = Math.toIntExact(roomRepository.countByStatus(RoomStatus.MAINTENANCE));

        return DashboardStatsResponse.builder()
                .totalRooms(totalRooms)
                .availableRooms(availableRooms)
                .occupiedRooms(occupiedRooms)
                .underMaintenance(underMaintenance)
                .build();
    }

    public List<DashboardScheduleItemResponse> getScheduleByDay(String day) {
        String normalizedDay = normalizeDay(day);
        return scheduleRepository.findByDayIgnoreCaseAndStatusIgnoreCaseOrderByStartTimeAsc(normalizedDay, "Active")
                .stream()
                .map(this::toScheduleItem)
                .toList();
    }

    private String normalizeDay(String day) {
        if (day == null || day.isBlank()) {
            return getTodayName();
        }
        return day.trim();
    }

    private String getTodayName() {
        return LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    private DashboardScheduleItemResponse toScheduleItem(ScheduleEntry entry) {
        return DashboardScheduleItemResponse.builder()
                .id(entry.getId())
                .code(entry.getSubjectCode())
                .subjectName(entry.getSubjectName())
                .faculty(entry.getFacultyName())
                .courseCode(entry.getCourseCode())
                .section(entry.getSection())
                .startTime(entry.getStartTime())
                .endTime(entry.getEndTime())
                .room(entry.getRoomName())
                .build();
    }
}
