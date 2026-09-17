package com.pup.bataan.cosmos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pup.bataan.cosmos.dto.request.CreateCurriculumRequest;
import com.pup.bataan.cosmos.dto.request.UpdateCurriculumRequest;
import com.pup.bataan.cosmos.dto.response.CurriculumResponse;
import com.pup.bataan.cosmos.entity.Curriculum;
import com.pup.bataan.cosmos.exception.ResourceNotFoundException;
import com.pup.bataan.cosmos.repository.CurriculumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurriculumService {

    private final CurriculumRepository curriculumRepository;
    private final ObjectMapper objectMapper;

    public List<CurriculumResponse> getAll() {
        return curriculumRepository.findAllByOrderByProgramAscYearLevelAscSemesterAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CurriculumResponse getById(Long id) {
        return toResponse(findEntityById(id));
    }

    public List<CurriculumResponse> getByProgram(String program) {
        return curriculumRepository.findByProgram(program)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CurriculumResponse> getByAcademicYear(String academicYear) {
        return curriculumRepository.findByAcademicYear(academicYear)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CurriculumResponse create(CreateCurriculumRequest request) {
        Curriculum curriculum = Curriculum.builder()
                .program(request.getProgram().trim())
                .yearLevel(request.getYearLevel().trim())
                .semester(request.getSemester().trim())
                .academicYear(request.getAcademicYear().trim())
                .status(request.getStatus() != null ? request.getStatus() : "Active")
                .description(request.getDescription() != null ? request.getDescription().trim() : "")
                .subjectsJson(serializeSubjects(request.getSubjects()))
                .build();

        return toResponse(curriculumRepository.save(curriculum));
    }

    @Transactional
    public CurriculumResponse update(Long id, UpdateCurriculumRequest request) {
        Curriculum curriculum = findEntityById(id);

        if (request.getProgram() != null && !request.getProgram().isBlank()) {
            curriculum.setProgram(request.getProgram().trim());
        }
        if (request.getYearLevel() != null && !request.getYearLevel().isBlank()) {
            curriculum.setYearLevel(request.getYearLevel().trim());
        }
        if (request.getSemester() != null && !request.getSemester().isBlank()) {
            curriculum.setSemester(request.getSemester().trim());
        }
        if (request.getAcademicYear() != null && !request.getAcademicYear().isBlank()) {
            curriculum.setAcademicYear(request.getAcademicYear().trim());
        }
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            curriculum.setStatus(request.getStatus().trim());
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            curriculum.setDescription(request.getDescription().trim());
        }
        if (request.getSubjects() != null && !request.getSubjects().isEmpty()) {
            curriculum.setSubjectsJson(serializeSubjects(request.getSubjects()));
        }

        return toResponse(curriculumRepository.save(curriculum));
    }

    @Transactional
    public void delete(Long id) {
        Curriculum curriculum = findEntityById(id);
        curriculumRepository.delete(curriculum);
    }

    private Curriculum findEntityById(Long id) {
        return curriculumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curriculum not found with id: " + id));
    }

    private String serializeSubjects(List<Map<String, Object>> subjects) {
        try {
            return objectMapper.writeValueAsString(subjects != null ? subjects : List.of());
        } catch (Exception e) {
            return "[]";
        }
    }

    private List<Map<String, Object>> deserializeSubjects(String subjectsJson) {
        if (subjectsJson == null || subjectsJson.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(subjectsJson, List.class);
        } catch (Exception e) {
            return List.of();
        }
    }

    private Integer calculateTotalUnits(List<Map<String, Object>> subjects) {
        if (subjects == null || subjects.isEmpty()) {
            return 0;
        }
        return subjects.stream()
                .mapToInt(subject -> {
                    Object units = subject.get("units");
                    if (units instanceof Number) {
                        return ((Number) units).intValue();
                    }
                    return 0;
                })
                .sum();
    }

    private CurriculumResponse toResponse(Curriculum curriculum) {
        List<Map<String, Object>> subjects = deserializeSubjects(curriculum.getSubjectsJson());
        Integer totalUnits = calculateTotalUnits(subjects);

        return CurriculumResponse.builder()
                .id(curriculum.getId())
                .program(curriculum.getProgram())
                .yearLevel(curriculum.getYearLevel())
                .semester(curriculum.getSemester())
                .academicYear(curriculum.getAcademicYear())
                .status(curriculum.getStatus())
                .description(curriculum.getDescription())
                .subjects(subjects)
                .totalSubjects(subjects.size())
                .totalUnits(totalUnits)
                .build();
    }
}
