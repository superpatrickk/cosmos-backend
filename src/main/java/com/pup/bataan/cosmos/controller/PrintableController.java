package com.pup.bataan.cosmos.controller;

import com.pup.bataan.cosmos.entity.ScheduleEntry;
import com.pup.bataan.cosmos.service.PrintableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class PrintableController {

    private final PrintableService printableService;

    @GetMapping("/printable")
    public ResponseEntity<List<ScheduleEntry>> getPrintable(
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String filterCourse,
            @RequestParam(required = false) String filterFaculty,
            @RequestParam(required = false) String filterRoom
    ) {
        List<ScheduleEntry> list = printableService.getPrintableSchedules(semester, academicYear, filterCourse, filterFaculty, filterRoom);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/export-pdf")
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String filterCourse,
            @RequestParam(required = false) String filterFaculty,
            @RequestParam(required = false) String filterRoom
    ) throws IOException {
        byte[] pdf = printableService.buildMultiFacultyPdfBytes(semester, academicYear, filterCourse, filterFaculty, filterRoom);
        if (pdf == null || pdf.length == 0) {
            return ResponseEntity.noContent().build();
        }

        String filename = "COSMOS_Schedules" + (semester != null ? "_" + semester.replaceAll("\\s+", "_") : "") + (academicYear != null ? "_" + academicYear.replaceAll("\\s+", "_") : "") + ".pdf";
        // Provide both a safe ASCII fallback and an RFC2231-encoded UTF-8 filename
        String asciiFallback = filename.replaceAll("[^\\x20-\\x7E]", "_");
        String utf8Enc = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        String headerValue = "attachment; filename=\"" + asciiFallback + "\"; filename*=UTF-8''" + utf8Enc;

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }
}
