package com.pup.bataan.cosmos.service;

import com.pup.bataan.cosmos.dto.request.CreateSubjectRequest;
import com.pup.bataan.cosmos.dto.response.SubjectResponse;
import com.pup.bataan.cosmos.entity.Subject;
import com.pup.bataan.cosmos.repository.SubjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private SubjectService subjectService;

    @Test
    void createPersistsAndReturnsAcademicYear() {
        when(subjectRepository.existsByCourseAndCode("BSCS", "CS101")).thenReturn(false);
        when(subjectRepository.save(any(Subject.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateSubjectRequest request = CreateSubjectRequest.builder()
                .code("CS101")
                .name("Introduction to Programming")
                .units(3)
                .lec(2)
                .lab(1)
                .type("Lec/Lab")
                .prerequisite("None")
                .course("BSCS")
                .yearLevel("1st Year")
                .academicYear("2025-2026")
                .semester("1st Semester")
                .description("Introduction to Programming")
                .build();

        SubjectResponse response = subjectService.create(request);

        ArgumentCaptor<Subject> subjectCaptor = ArgumentCaptor.forClass(Subject.class);
        verify(subjectRepository).save(subjectCaptor.capture());
        assertEquals("2025-2026", subjectCaptor.getValue().getAcademicYear());
        assertEquals(2, subjectCaptor.getValue().getLec());
        assertEquals(1, subjectCaptor.getValue().getLab());
        assertEquals("2025-2026", response.getAcademicYear());
        assertEquals(2, response.getLec());
        assertEquals(1, response.getLab());
    }
}
