package com.pup.bataan.cosmos.service;

import com.pup.bataan.cosmos.entity.ScheduleEntry;
import com.pup.bataan.cosmos.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import java.io.InputStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrintableService {

    private final ScheduleRepository scheduleRepository;

    public List<ScheduleEntry> getPrintableSchedules(String semester, String academicYear, String course, String faculty, String room) {
        List<ScheduleEntry> all = scheduleRepository.findAll();
        return all.stream()
                .filter(s -> semester == null || semester.isBlank() || semester.equals(s.getSemester()))
                .filter(s -> academicYear == null || academicYear.isBlank() || academicYear.equals(s.getAcademicYear()))
                .filter(s -> course == null || course.isBlank() || course.equals("All Courses") || course.equals(s.getCourseCode()))
                .filter(s -> faculty == null || faculty.isBlank() || faculty.equals("All Faculty") || faculty.equals(s.getFacultyName()))
                .filter(s -> room == null || room.isBlank() || room.equals("All Rooms") || room.equals(s.getRoomName()))
                .sorted(Comparator.comparing(ScheduleEntry::getFacultyName).thenComparing(ScheduleEntry::getDay))
                .collect(Collectors.toList());
    }

    public byte[] buildMultiFacultyPdfBytes(String semester, String academicYear, String course, String faculty, String room) throws IOException {
        List<ScheduleEntry> filtered = getPrintableSchedules(semester, academicYear, course, faculty, room);
        if (filtered.isEmpty()) return new byte[0];

        Map<String, List<ScheduleEntry>> byFaculty = filtered.stream()
                .collect(Collectors.groupingBy(ScheduleEntry::getFacultyName, LinkedHashMap::new, Collectors.toList()));

        try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            for (Map.Entry<String, List<ScheduleEntry>> e : byFaculty.entrySet()) {
                addFacultyPages(document, e.getKey(), e.getValue(), semester, academicYear);
            }
            document.save(out);
            return out.toByteArray();
        }
    }

    private void addFacultyPages(PDDocument document, String facultyName, List<ScheduleEntry> schedules, String semester, String academicYear) throws IOException {
        // Layout constants
        final float margin = 50f;
        final float headerHeight = 100f;
        final float footerHeight = 40f;

        // Try to load an embedded TTF from resources for better typography; fall back to standard 14 fonts
        PDFont headerFont;
        PDFont normalFont;
        PDFont smallFont;
        InputStream ttf = getClass().getClassLoader().getResourceAsStream("fonts/DejaVuSans.ttf");
        if (ttf != null) {
            try {
                PDType0Font loaded = PDType0Font.load(document, ttf, true);
                headerFont = loaded;
                normalFont = loaded;
                smallFont = loaded;
            } catch (Exception ex) {
                headerFont = new PDType1Font(FontName.HELVETICA_BOLD);
                normalFont = new PDType1Font(FontName.HELVETICA);
                smallFont = new PDType1Font(FontName.HELVETICA_OBLIQUE);
            }
        } else {
            headerFont = new PDType1Font(FontName.HELVETICA_BOLD);
            normalFont = new PDType1Font(FontName.HELVETICA);
            smallFont = new PDType1Font(FontName.HELVETICA_OBLIQUE);
        }

        PDRectangle media = PDRectangle.LETTER;
        float pageWidth = media.getWidth();
        float pageHeight = media.getHeight();

        float tableTopY = pageHeight - margin - 60; // below header area

        // Column layout
        float colDay = margin;
        float colTime = margin + 70;
        float colSubject = margin + 140;
        float colRoom = margin + 420;
        float colSection = margin + 500;
        float tableWidth = pageWidth - margin * 2;

        int pageIndex = 0;
        PDPageContentStream content = null;
        try {
            float y = tableTopY;
            for (int i = 0; i < schedules.size(); i++) {
                ScheduleEntry schedule = schedules.get(i);

                // Prepare subject lines wrapped to column width
                String subjText = schedule.getSubjectCode() + " " + schedule.getSubjectName();
                List<String> subjLines = wrapText(normalFont, 10, subjText, colRoom - colSubject - 8);
                int lineCount = Math.max(1, subjLines.size());
                float lineHeight = 12f;
                float neededHeight = lineCount * lineHeight + 8f; // padding

                // Start a new page if needed
                if (content == null || y - neededHeight < margin + footerHeight) {
                    if (content != null) {
                        // add footer for previous page
                        content.setFont(smallFont, 9);
                        content.beginText(); content.newLineAtOffset(margin, margin - 10 + footerHeight / 2 - 6); content.showText("Generated by COSMOS"); content.endText();
                        content.beginText(); content.newLineAtOffset(pageWidth - margin - 120, margin - 10 + footerHeight / 2 - 6); content.showText("Page " + pageIndex); content.endText();
                        content.close();
                    }
                    PDPage page = new PDPage(media);
                    document.addPage(page);
                    pageIndex++;
                    content = new PDPageContentStream(document, page, AppendMode.OVERWRITE, true);
                    y = pageHeight - margin;

                    // Draw header on new page
                    content.setLineWidth(1f);
                    content.addRect(margin / 2f, margin / 2f, pageWidth - margin, pageHeight - margin);
                    content.stroke();
                    try {
                        PDImageXObject logoImage = PDImageXObject.createFromFile("src/main/resources/static/campus-logo.png", document);
                        content.drawImage(logoImage, margin, y - 60, 60, 60);
                    } catch (Exception ignored) {}

                    // Title and faculty
                    content.setFont(headerFont, 14);
                    content.beginText(); content.newLineAtOffset(margin + 80, y - 10); content.showText("POLYTECHNIC UNIVERSITY OF THE PHILIPPINES — Faculty Schedule"); content.endText();
                    content.setFont(headerFont, 12);
                    content.beginText(); content.newLineAtOffset(margin, y - 36); content.showText("Faculty: " + Objects.toString(facultyName, "-")); content.endText();
                    content.setFont(normalFont, 10);
                    content.beginText(); content.newLineAtOffset(pageWidth - margin - 220, y - 36); content.showText((semester != null ? semester : "") + " • " + (academicYear != null ? academicYear : "")); content.endText();

                    // Table header
                    y -= headerHeight - 20;
                    content.setNonStrokingColor(240f / 255f, 240f / 255f, 240f / 255f);
                    content.addRect(margin, y - 14, tableWidth, 16);
                    content.fill();
                    content.setNonStrokingColor(0f, 0f, 0f);
                    content.setFont(headerFont, 11);
                    content.beginText(); content.newLineAtOffset(colDay + 2, y); content.showText("DAY"); content.endText();
                    content.beginText(); content.newLineAtOffset(colTime + 2, y); content.showText("TIME"); content.endText();
                    content.beginText(); content.newLineAtOffset(colSubject + 2, y); content.showText("SUBJECT"); content.endText();
                    content.beginText(); content.newLineAtOffset(colRoom + 2, y); content.showText("ROOM"); content.endText();
                    content.beginText(); content.newLineAtOffset(colSection + 2, y); content.showText("SECTION"); content.endText();
                    y -= 18;
                }

                // Draw row border
                content.addRect(margin, y - neededHeight + 4, tableWidth, neededHeight);
                content.stroke();

                // DAY
                content.setFont(normalFont, 10);
                content.beginText(); content.newLineAtOffset(colDay + 2, y - 2); content.showText(Objects.toString(schedule.getDay(), "-")); content.endText();
                // TIME
                content.beginText(); content.newLineAtOffset(colTime + 2, y - 2); content.showText(schedule.getStartTime() + " - " + schedule.getEndTime()); content.endText();
                // SUBJECT - multiple lines
                float textY = y - 2;
                for (String line : subjLines) {
                    content.beginText(); content.newLineAtOffset(colSubject + 2, textY); content.showText(line); content.endText();
                    textY -= 12f;
                }
                // ROOM
                content.beginText(); content.newLineAtOffset(colRoom + 2, y - 2); content.showText(Objects.toString(schedule.getRoomName(), "-")); content.endText();
                // SECTION
                content.beginText(); content.newLineAtOffset(colSection + 2, y - 2); content.showText(schedule.getCourseCode() + " " + schedule.getSection()); content.endText();

                y -= neededHeight;
            }
            // close last open content if any
            if (content != null) {
                content.setFont(smallFont, 9);
                content.beginText(); content.newLineAtOffset(margin, margin - 10 + footerHeight / 2 - 6); content.showText("Generated by COSMOS"); content.endText();
                content.beginText(); content.newLineAtOffset(pageWidth - margin - 120, margin - 10 + footerHeight / 2 - 6); content.showText("Page " + pageIndex); content.endText();
                content.close();
            }
        } finally {
            if (content != null) content.close();
        }
    }

    private List<String> wrapText(PDFont font, float fontSize, String text, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isBlank()) {
            lines.add("");
            return lines;
        }
        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        for (String w : words) {
            String candidate = line.length() == 0 ? w : line + " " + w;
            float wWidth = font.getStringWidth(candidate) / 1000 * fontSize;
            if (wWidth <= maxWidth) {
                if (line.length() > 0) line.append(' ');
                line.append(w);
            } else {
                if (line.length() > 0) {
                    lines.add(line.toString());
                    line = new StringBuilder(w);
                } else {
                    // single word too long: force split
                    String part = w;
                    while (font.getStringWidth(part) / 1000 * fontSize > maxWidth && part.length() > 1) {
                        part = part.substring(0, part.length() - 1);
                    }
                    lines.add(part + "-");
                    String rest = w.substring(part.length());
                    line = new StringBuilder(rest);
                }
            }
        }
        if (line.length() > 0) lines.add(line.toString());
        return lines;
    }
}
