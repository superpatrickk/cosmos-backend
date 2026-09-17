package com.pup.bataan.cosmos.service;

import com.pup.bataan.cosmos.entity.FacultyMember;
import com.pup.bataan.cosmos.entity.ScheduleEntry;
import com.pup.bataan.cosmos.repository.FacultyMemberRepository;
import com.pup.bataan.cosmos.repository.ScheduleRepository;
import jakarta.activation.DataHandler;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final FacultyMemberRepository facultyMemberRepository;
    private final ScheduleRepository scheduleRepository;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    private static final Map<String, Integer> DAY_ORDER = Map.of(
            "Monday", 1,
            "Tuesday", 2,
            "Wednesday", 3,
            "Thursday", 4,
            "Friday", 5,
            "Saturday", 6,
            "Sunday", 7
    );

    @Transactional(readOnly = true)
    public int sendScheduleToFaculty(Long facultyId) {
        FacultyMember faculty = facultyMemberRepository.findById(facultyId)
                .orElseThrow(() -> new IllegalArgumentException("Faculty member not found."));

        List<ScheduleEntry> schedules = getFacultySchedules(faculty.getName());
        if (schedules.isEmpty()) {
            throw new IllegalArgumentException("No scheduled classes were found for this faculty member.");
        }

        if (faculty.getEmail() == null || faculty.getEmail().isBlank()) {
            throw new IllegalArgumentException("This faculty member has no registered email address.");
        }

        sendHtmlEmail(
                faculty.getEmail(),
                "Your Teaching Schedule",
                buildScheduleEmailBody(faculty, schedules),
                faculty,
                schedules
        );

        return schedules.size();
    }

    @Transactional(readOnly = true)
    public int sendScheduleToAllFaculty() {
        List<FacultyMember> facultyMembers = facultyMemberRepository.findAllByOrderByNameAsc();
        int sentCount = 0;

        for (FacultyMember faculty : facultyMembers) {
            if (faculty.getEmail() == null || faculty.getEmail().isBlank()) {
                continue;
            }

            List<ScheduleEntry> schedules = getFacultySchedules(faculty.getName());
            if (schedules.isEmpty()) {
                continue;
            }

            sendHtmlEmail(
                    faculty.getEmail(),
                    "Your Teaching Schedule",
                    buildScheduleEmailBody(faculty, schedules),
                    faculty,
                    schedules
            );
            sentCount++;
        }

        if (sentCount == 0) {
            throw new IllegalArgumentException("No faculty schedule emails could be sent because no valid faculty schedules were found.");
        }

        return sentCount;
    }

    private List<ScheduleEntry> getFacultySchedules(String facultyName) {
        return scheduleRepository.findByFacultyNameContainingIgnoreCase(facultyName)
                .stream()
                .sorted(Comparator
                        .comparingInt((ScheduleEntry schedule) -> DAY_ORDER.getOrDefault(schedule.getDay(), 99))
                        .thenComparing(ScheduleEntry::getStartTime)
                        .thenComparing(ScheduleEntry::getSubjectCode))
                .toList();
    }

    private String buildScheduleEmailBody(FacultyMember faculty, List<ScheduleEntry> schedules) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style=\"font-family: Arial, sans-serif; color: #222; line-height: 1.6;\">")
                .append("<div style=\"max-width: 720px; margin: 0 auto; padding: 24px; border: 1px solid #eee; border-radius: 12px;\">")
                .append("<h2 style=\"margin-bottom: 12px; color: #7d1f1f;\">COSMOS Faculty Schedule</h2>")
                .append("<p>Dear <strong>")
                .append(escapeHtml(faculty.getName()))
                .append("</strong>,</p>")
                .append("<p>Your assigned classes for the current semester are listed below.</p>")
                .append("<table style=\"width: 100%; border-collapse: collapse; margin-top: 18px; font-size: 14px;\">")
                .append("<thead><tr style=\"background: #f7f7f7; text-align: left;\">")
                .append("<th style=\"padding: 10px; border: 1px solid #ddd;\">Day</th>")
                .append("<th style=\"padding: 10px; border: 1px solid #ddd;\">Subject</th>")
                .append("<th style=\"padding: 10px; border: 1px solid #ddd;\">Time</th>")
                .append("<th style=\"padding: 10px; border: 1px solid #ddd;\">Room</th>")
                .append("<th style=\"padding: 10px; border: 1px solid #ddd;\">Section</th>")
                .append("</tr></thead><tbody>");

        for (ScheduleEntry schedule : schedules) {
            html.append("<tr>")
                    .append("<td style=\"padding: 10px; border: 1px solid #ddd;\">")
                    .append(escapeHtml(Objects.toString(schedule.getDay(), "-")))
                    .append("</td>")
                    .append("<td style=\"padding: 10px; border: 1px solid #ddd;\"><strong>")
                    .append(escapeHtml(Objects.toString(schedule.getSubjectCode(), "-")))
                    .append("</strong><br>")
                    .append(escapeHtml(Objects.toString(schedule.getSubjectName(), "-")))
                    .append("</td>")
                    .append("<td style=\"padding: 10px; border: 1px solid #ddd;\">")
                    .append(escapeHtml(Objects.toString(schedule.getStartTime(), "-")))
                    .append(" - ")
                    .append(escapeHtml(Objects.toString(schedule.getEndTime(), "-")))
                    .append("</td>")
                    .append("<td style=\"padding: 10px; border: 1px solid #ddd;\">")
                    .append(escapeHtml(Objects.toString(schedule.getRoomName(), "-")))
                    .append("</td>")
                    .append("<td style=\"padding: 10px; border: 1px solid #ddd;\">")
                    .append(escapeHtml(Objects.toString(schedule.getCourseCode(), "-")))
                    .append(" / ")
                    .append(escapeHtml(Objects.toString(schedule.getSection(), "-")))
                    .append("</td>")
                    .append("</tr>");
        }

        html.append("</tbody></table>")
                .append("<p style=\"margin-top: 20px;\">Thank you,<br><strong>COSMOS Academic Scheduling Team</strong></p>")
                .append("</div>")
                .append("</body></html>");

        return html.toString();
    }

    private void sendHtmlEmail(String toEmail, String subject, String htmlBody, FacultyMember faculty, List<ScheduleEntry> schedules) {
        if (fromEmail == null || fromEmail.isBlank() || mailPassword == null || mailPassword.isBlank()) {
            throw new IllegalStateException(
                    "SMTP credentials are not configured. Set COSMOS_MAIL_USERNAME and COSMOS_MAIL_PASSWORD before sending emails."
            );
        }

        try {
            byte[] pdfBytes = buildSchedulePdfBytes(faculty, schedules);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            mimeMessage.setFrom(fromEmail);
            mimeMessage.setRecipients(MimeMessage.RecipientType.TO, toEmail);
            mimeMessage.setSubject(subject, StandardCharsets.UTF_8.name());

            // Build multipart manually so we can set RFC2231 filename for attachments
            jakarta.mail.Multipart multipart = new jakarta.mail.internet.MimeMultipart();

            jakarta.mail.BodyPart htmlPart = new jakarta.mail.internet.MimeBodyPart();
            htmlPart.setContent(htmlBody, "text/html; charset=UTF-8");
            multipart.addBodyPart(htmlPart);

            String safeName = sanitizeForFilename(Objects.toString(faculty.getName(), "faculty"));
            String attachmentName = safeName + " - Schedule.pdf";
            String asciiFallback = attachmentName.replaceAll("[^\\x20-\\x7E]", "_");
            String utf8Enc = java.net.URLEncoder.encode(attachmentName, StandardCharsets.UTF_8);

            jakarta.mail.BodyPart attachPart = new jakarta.mail.internet.MimeBodyPart();
            ByteArrayDataSource ds = new ByteArrayDataSource(pdfBytes, "application/pdf");
            attachPart.setDataHandler(new DataHandler(ds));
            // set a safe fallback filename and a RFC2231 UTF-8 filename*
            attachPart.setFileName(asciiFallback);
            attachPart.setHeader("Content-Disposition", "attachment; filename=\"" + asciiFallback + "\"; filename*=UTF-8''" + utf8Enc);
            multipart.addBodyPart(attachPart);

            mimeMessage.setContent(multipart);
            mailSender.send(mimeMessage);
        } catch (MailException | MessagingException | IOException e) {
            log.error("Failed to send schedule email to {}: {}", toEmail, e.getMessage(), e);
            throw new IllegalStateException(
                    "Unable to send schedule email to " + toEmail + ". Check SMTP credentials and mailbox settings. Root cause: " + e.getMessage(),
                    e
            );
        }
    }

    private byte[] buildSchedulePdfBytes(FacultyMember faculty, List<ScheduleEntry> schedules) throws IOException {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);
            try {
                float margin = 50;
                float pageWidth = page.getMediaBox().getWidth();
                float pageHeight = page.getMediaBox().getHeight();
                float y = pageHeight - margin;
                float lineGap = 16;
                float rowHeight = 18;

                // Outer frame
                content.setLineWidth(2f);
                content.addRect(margin / 2f, margin / 2f, pageWidth - margin, pageHeight - margin);
                content.stroke();

                // Top logo area
                float logoSize = 60;
                float logoX = margin;
                float logoY = y - logoSize;
                PDImageXObject logoImage = PDImageXObject.createFromFile("src/main/resources/static/campus-logo.png", document);
                content.drawImage(logoImage, logoX, logoY, logoSize, logoSize);

                content.setFont(new PDType1Font(FontName.HELVETICA_BOLD), 18);
                content.beginText();
                content.newLineAtOffset(margin + logoSize + 15, y - 20);
                content.showText("POLYTECHNIC UNIVERSITY OF THE PHILIPPINES");
                content.endText();

                content.setFont(new PDType1Font(FontName.HELVETICA), 10);
                content.beginText();
                content.newLineAtOffset(margin + logoSize + 15, y - 38);
                content.showText("Bataan Campus");
                content.endText();

                y -= logoSize + lineGap;

                content.setLineWidth(1f);
                content.moveTo(margin, y);
                content.lineTo(pageWidth - margin, y);
                content.stroke();

                y -= lineGap;
                // Metadata row
                content.setFont(new PDType1Font(FontName.HELVETICA_BOLD), 12);
                content.beginText();
                content.newLineAtOffset(margin, y);
                content.showText("Faculty:");
                content.endText();

                content.setFont(new PDType1Font(FontName.HELVETICA), 12);
                content.beginText();
                content.newLineAtOffset(margin + 70, y);
                content.showText(Objects.toString(faculty.getName(), "-"));
                content.endText();

                content.setFont(new PDType1Font(FontName.HELVETICA_BOLD), 12);
                content.beginText();
                content.newLineAtOffset(pageWidth / 2, y);
                content.showText("Term:");
                content.endText();

                content.setFont(new PDType1Font(FontName.HELVETICA), 12);
                content.beginText();
                content.newLineAtOffset(pageWidth / 2 + 50, y);
                content.showText("First Semester");
                content.endText();

                y -= lineGap;
                content.setFont(new PDType1Font(FontName.HELVETICA_BOLD), 12);
                content.beginText();
                content.newLineAtOffset(margin, y);
                content.showText("Department:");
                content.endText();

                content.setFont(new PDType1Font(FontName.HELVETICA), 12);
                content.beginText();
                content.newLineAtOffset(margin + 90, y);
                content.showText(Objects.toString(faculty.getDepartment(), "Academic Affairs"));
                content.endText();

                content.setFont(new PDType1Font(FontName.HELVETICA_BOLD), 12);
                content.beginText();
                content.newLineAtOffset(pageWidth / 2, y);
                content.showText("Academic Year:");
                content.endText();

                content.setFont(new PDType1Font(FontName.HELVETICA), 12);
                content.beginText();
                content.newLineAtOffset(pageWidth / 2 + 100, y);
                content.showText("2025-2026");
                content.endText();

                y -= lineGap * 2;
                content.moveTo(margin, y);
                content.lineTo(pageWidth - margin, y);
                content.stroke();

                y -= lineGap;
                // Table header
                float colDay = margin;
                float colSubject = margin + 80;
                float colTime = margin + 290;
                float colRoom = margin + 380;
                float colSection = margin + 460;

                content.setNonStrokingColor(240f / 255f, 240f / 255f, 240f / 255f);
                content.addRect(margin, y - rowHeight + 4, pageWidth - margin * 2, rowHeight);
                content.fill();
                content.setNonStrokingColor(0f, 0f, 0f);

                content.setFont(new PDType1Font(FontName.HELVETICA_BOLD), 11);
                content.beginText();
                content.newLineAtOffset(colDay, y);
                content.showText("DAY");
                content.endText();
                content.beginText();
                content.newLineAtOffset(colSubject, y);
                content.showText("SUBJECT");
                content.endText();
                content.beginText();
                content.newLineAtOffset(colTime, y);
                content.showText("TIME");
                content.endText();
                content.beginText();
                content.newLineAtOffset(colRoom, y);
                content.showText("ROOM");
                content.endText();
                content.beginText();
                content.newLineAtOffset(colSection, y);
                content.showText("SECTION");
                content.endText();

                y -= rowHeight;
                content.setFont(new PDType1Font(FontName.HELVETICA), 11);

                for (ScheduleEntry schedule : schedules) {
                    if (y < margin + rowHeight * 3) {
                        break;
                    }
                    content.beginText();
                    content.newLineAtOffset(colDay, y);
                    content.showText(Objects.toString(schedule.getDay(), "-"));
                    content.endText();

                    content.beginText();
                    content.newLineAtOffset(colSubject, y);
                    content.showText(Objects.toString(schedule.getSubjectCode(), "-") + " " + Objects.toString(schedule.getSubjectName(), "-"));
                    content.endText();

                    content.beginText();
                    content.newLineAtOffset(colTime, y);
                    content.showText(Objects.toString(schedule.getStartTime(), "-") + " - " + Objects.toString(schedule.getEndTime(), "-"));
                    content.endText();

                    content.beginText();
                    content.newLineAtOffset(colRoom, y);
                    content.showText(Objects.toString(schedule.getRoomName(), "-"));
                    content.endText();

                    content.beginText();
                    content.newLineAtOffset(colSection, y);
                    content.showText(Objects.toString(schedule.getCourseCode(), "-") + " " + Objects.toString(schedule.getSection(), "-"));
                    content.endText();

                    y -= rowHeight;
                }

                y -= lineGap * 1.5;
                content.setFont(new PDType1Font(FontName.HELVETICA_OBLIQUE), 10);
                content.beginText();
                content.newLineAtOffset(margin, y);
                content.showText("This document is generated by COSMOS. It contains only schedule-related details.");
                content.endText();

            } finally {
                content.close();
            }

            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }

    private String sanitizeForFilename(String input) {
        if (input == null) return "faculty";
        // Replace characters that are unsafe in filenames with underscore
        String sanitized = input.replaceAll("[\\\\/:*?\"<>|\\s]+", "_").trim();
        if (sanitized.isEmpty()) return "faculty";
        // Limit length to avoid overly long filenames
        return sanitized.length() > 100 ? sanitized.substring(0, 100) : sanitized;
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
