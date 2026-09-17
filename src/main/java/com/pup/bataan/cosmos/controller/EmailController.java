package com.pup.bataan.cosmos.controller;

import com.pup.bataan.cosmos.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send-schedule/{facultyId}")
    public ResponseEntity<Map<String, Object>> sendScheduleToFaculty(@PathVariable Long facultyId) {
        int count = emailService.sendScheduleToFaculty(facultyId);
        return ResponseEntity.ok(Map.of(
                "sentCount", count,
                "message", "Schedule email sent successfully."
        ));
    }

    @PostMapping("/send-schedule/all")
    public ResponseEntity<Map<String, Object>> sendScheduleToAllFaculty() {
        int count = emailService.sendScheduleToAllFaculty();
        return ResponseEntity.ok(Map.of(
                "sentCount", count,
                "message", "Schedule emails sent successfully to all faculty members."
        ));
    }
}
