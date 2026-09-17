package com.pup.bataan.cosmos.controller;

import com.pup.bataan.cosmos.dto.response.DashboardScheduleItemResponse;
import com.pup.bataan.cosmos.dto.response.DashboardStatsResponse;
import com.pup.bataan.cosmos.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> getStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }

    @GetMapping("/schedule")
    public ResponseEntity<List<DashboardScheduleItemResponse>> getSchedule(
            @RequestParam(required = false) String day) {
        return ResponseEntity.ok(dashboardService.getScheduleByDay(day));
    }
}
