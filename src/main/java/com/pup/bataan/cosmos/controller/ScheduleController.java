package com.pup.bataan.cosmos.controller;

import com.pup.bataan.cosmos.dto.request.CreateScheduleRequest;
import com.pup.bataan.cosmos.dto.request.UpdateScheduleRequest;
import com.pup.bataan.cosmos.entity.ScheduleEntry;
import com.pup.bataan.cosmos.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<ScheduleEntry>> getAll(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(scheduleService.getAll(search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleEntry> getById(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ScheduleEntry> create(@Valid @RequestBody CreateScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleEntry> update(@PathVariable Long id, @RequestBody UpdateScheduleRequest request) {
        return ResponseEntity.ok(scheduleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
