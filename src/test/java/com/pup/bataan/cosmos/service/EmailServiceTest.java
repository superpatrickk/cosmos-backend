package com.pup.bataan.cosmos.service;

import com.pup.bataan.cosmos.entity.FacultyMember;
import com.pup.bataan.cosmos.entity.ScheduleEntry;
import com.pup.bataan.cosmos.exception.ApiExceptionHandler;
import com.pup.bataan.cosmos.repository.FacultyMemberRepository;
import com.pup.bataan.cosmos.repository.ScheduleRepository;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private FacultyMemberRepository facultyMemberRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private org.springframework.mail.javamail.JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendScheduleToFaculty_shouldSendEmail_whenFacultyHasSchedule() {
        FacultyMember faculty = FacultyMember.builder()
                .id(1L)
                .name("Dr. Jane Doe")
                .department("Computer Science")
                .email("jane.doe@example.com")
                .phone("09123456789")
                .specialization("Algorithms")
                .build();

        ScheduleEntry schedule = ScheduleEntry.builder()
                .id(1L)
                .facultyName("Dr. Jane Doe")
                .subjectCode("CS101")
                .subjectName("Introduction to Programming")
                .roomName("Room 101")
                .day("Monday")
                .startTime("08:00")
                .endTime("10:00")
                .courseCode("BSCS")
                .section("A")
                .build();

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(facultyMemberRepository.findById(1L)).thenReturn(Optional.of(faculty));
        when(scheduleRepository.findByFacultyNameContainingIgnoreCase("Dr. Jane Doe")).thenReturn(List.of(schedule));
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        int sentCount = emailService.sendScheduleToFaculty(1L);

        assertEquals(1, sentCount);
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    void apiExceptionHandler_shouldReturnMailErrorDetails_whenEmailSendFails() {
        ApiExceptionHandler exceptionHandler = new ApiExceptionHandler();
        HttpServletRequest request = new MockHttpServletRequest("POST", "/api/email/send-schedule/1");

        ResponseEntity<java.util.Map<String, Object>> response = exceptionHandler.handleMailFailure(
                new IllegalStateException("SMTP credentials are not configured. Set COSMOS_MAIL_USERNAME and COSMOS_MAIL_PASSWORD."),
                request
        );

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("SMTP credentials are not configured. Set COSMOS_MAIL_USERNAME and COSMOS_MAIL_PASSWORD.",
                response.getBody().get("message"));
    }
}
