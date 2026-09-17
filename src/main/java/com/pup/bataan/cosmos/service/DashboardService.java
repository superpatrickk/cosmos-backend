package com.pup.bataan.cosmos.service;

import com.pup.bataan.cosmos.dto.response.DashboardScheduleItemResponse;
import com.pup.bataan.cosmos.dto.response.DashboardStatsResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class DashboardService {

    private static final Map<String, List<DashboardScheduleItemResponse>> SCHEDULE_BY_DAY = Map.of(
            "monday", List.of(
                    DashboardScheduleItemResponse.builder().id(1L).code("CS101").students(35).startTime("8:00 AM").endTime("10:00 AM").room("RM002").build(),
                    DashboardScheduleItemResponse.builder().id(2L).code("MATH101").students(40).startTime("10:00 AM").endTime("12:00 PM").room("RM003").build(),
                    DashboardScheduleItemResponse.builder().id(3L).code("EE201").students(28).startTime("1:00 PM").endTime("3:00 PM").room("RM005").build(),
                    DashboardScheduleItemResponse.builder().id(4L).code("BA105").students(45).startTime("3:00 PM").endTime("5:00 PM").room("RM001").build()
            ),
            "tuesday", List.of(
                    DashboardScheduleItemResponse.builder().id(5L).code("CS201").students(30).startTime("8:00 AM").endTime("10:00 AM").room("RM004").build(),
                    DashboardScheduleItemResponse.builder().id(6L).code("EE201").students(28).startTime("11:00 AM").endTime("1:00 PM").room("RM002").build()
            ),
            "wednesday", List.of(
                    DashboardScheduleItemResponse.builder().id(7L).code("BA105").students(45).startTime("9:00 AM").endTime("11:00 AM").room("RM001").build(),
                    DashboardScheduleItemResponse.builder().id(8L).code("MATH101").students(40).startTime("1:00 PM").endTime("3:00 PM").room("RM003").build(),
                    DashboardScheduleItemResponse.builder().id(9L).code("CS101").students(35).startTime("3:00 PM").endTime("5:00 PM").room("RM005").build()
            ),
            "thursday", List.of(
                    DashboardScheduleItemResponse.builder().id(10L).code("CS201").students(30).startTime("8:00 AM").endTime("10:00 AM").room("RM002").build()
            ),
            "friday", List.of(
                    DashboardScheduleItemResponse.builder().id(11L).code("EE201").students(28).startTime("10:00 AM").endTime("12:00 PM").room("RM004").build(),
                    DashboardScheduleItemResponse.builder().id(12L).code("BA105").students(45).startTime("2:00 PM").endTime("4:00 PM").room("RM001").build()
            )
    );

    public DashboardStatsResponse getStats() {
        List<DashboardScheduleItemResponse> todaySchedule = getScheduleByDay(null);
        int occupiedRooms = (int) todaySchedule.stream()
                .map(DashboardScheduleItemResponse::getRoom)
                .distinct()
                .count();

        int totalRooms = 48;
        int underMaintenance = 2;
        int availableRooms = Math.max(0, totalRooms - occupiedRooms - underMaintenance);

        return DashboardStatsResponse.builder()
                .totalRooms(totalRooms)
                .availableRooms(availableRooms)
                .occupiedRooms(occupiedRooms)
                .underMaintenance(underMaintenance)
                .build();
    }

    public List<DashboardScheduleItemResponse> getScheduleByDay(String day) {
        String normalizedDay = normalizeDay(day);
        return new ArrayList<>(SCHEDULE_BY_DAY.getOrDefault(normalizedDay, List.of()));
    }

    private String normalizeDay(String day) {
        if (day == null || day.isBlank()) {
            return getTodayName();
        }

        String normalized = day.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "monday" -> "monday";
            case "tuesday" -> "tuesday";
            case "wednesday" -> "wednesday";
            case "thursday" -> "thursday";
            case "friday" -> "friday";
            default -> getTodayName();
        };
    }

    private String getTodayName() {
        return switch (LocalDate.now().getDayOfWeek()) {
            case MONDAY -> "monday";
            case TUESDAY -> "tuesday";
            case WEDNESDAY -> "wednesday";
            case THURSDAY -> "thursday";
            case FRIDAY -> "friday";
            default -> "monday";
        };
    }
}
