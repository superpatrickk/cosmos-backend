package com.pup.bataan.cosmos.service;

import com.pup.bataan.cosmos.dto.request.CreateSubjectRequest;
import com.pup.bataan.cosmos.dto.request.UpdateSubjectRequest;
import com.pup.bataan.cosmos.dto.response.SubjectResponse;
import com.pup.bataan.cosmos.entity.Subject;
import com.pup.bataan.cosmos.exception.ResourceNotFoundException;
import com.pup.bataan.cosmos.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public List<SubjectResponse> getAll(String search) {
        List<Subject> subjects;
        if (search == null || search.isBlank()) {
            subjects = subjectRepository.findAllByOrderByCodeAsc();
        } else {
            String q = search.trim();
                subjects = subjectRepository.findByCodeContainingIgnoreCaseOrNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCourseContainingIgnoreCaseOrYearLevelContainingIgnoreCase(
                    q, q, q, q, q
                );
        }
        return subjects.stream().map(this::toResponse).toList();
    }

    public SubjectResponse getById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public SubjectResponse create(CreateSubjectRequest request) {
        String normalizedCode = request.getCode().trim().toUpperCase();
        String normalizedCourse = request.getCourse().trim().toUpperCase();

        if (subjectRepository.existsByCourseAndCode(normalizedCourse, normalizedCode)) {
            throw new DataIntegrityViolationException("This subject already exists for the selected program.");
        }

        Subject subject = Subject.builder()
                .code(normalizedCode)
                .name(request.getName() == null || request.getName().isBlank()
                        ? request.getDescription().trim()
                        : request.getName().trim())
                .units(request.getUnits() != null ? request.getUnits() : 3)
                .type(request.getType().trim())
                .prerequisite(request.getPrerequisite().trim())
                .course(normalizedCourse)
                .yearLevel(request.getYearLevel().trim())
                .semester(request.getSemester().trim())
                .description(request.getDescription().trim())
                .status(request.getStatus() == null || request.getStatus().isBlank() ? "Active" : request.getStatus().trim())
                .build();
        return toResponse(subjectRepository.save(subject));
    }

    @Transactional
    public SubjectResponse update(Long id, UpdateSubjectRequest request) {
        Subject subject = findEntityById(id);
        String updatedCode = subject.getCode();
        String updatedCourse = subject.getCourse();

        if (request.getCode() != null && !request.getCode().isBlank()) {
            updatedCode = request.getCode().trim().toUpperCase();
        }

        if (request.getCourse() != null && !request.getCourse().isBlank()) {
            updatedCourse = request.getCourse().trim().toUpperCase();
        }

        if (!updatedCode.equals(subject.getCode()) || !updatedCourse.equals(subject.getCourse())) {
            if (subjectRepository.existsByCourseAndCode(updatedCourse, updatedCode)) {
                throw new DataIntegrityViolationException("This subject already exists for the selected program.");
            }
        }

        if (request.getCode() != null && !request.getCode().isBlank()) {
            subject.setCode(updatedCode);
        }
        if (request.getName() != null && !request.getName().isBlank()) {
            subject.setName(request.getName().trim());
        }
        if (request.getUnits() != null) {
            subject.setUnits(request.getUnits());
        }
        if (request.getType() != null && !request.getType().isBlank()) {
            subject.setType(request.getType().trim());
        }
        if (request.getPrerequisite() != null && !request.getPrerequisite().isBlank()) {
            subject.setPrerequisite(request.getPrerequisite().trim());
        }
        if (request.getCourse() != null && !request.getCourse().isBlank()) {
            subject.setCourse(updatedCourse);
        }
        if (request.getYearLevel() != null && !request.getYearLevel().isBlank()) {
            subject.setYearLevel(request.getYearLevel().trim());
        }
        if (request.getSemester() != null && !request.getSemester().isBlank()) {
            subject.setSemester(request.getSemester().trim());
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            subject.setDescription(request.getDescription().trim());
        }
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            subject.setStatus(request.getStatus().trim());
        }
        return toResponse(subjectRepository.save(subject));
    }

    @Transactional
    public void delete(Long id) {
        Subject subject = findEntityById(id);
        subjectRepository.delete(subject);
    }

    private Subject findEntityById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));
    }

    private SubjectResponse toResponse(Subject subject) {
        return SubjectResponse.builder()
                .id(subject.getId())
                .code(subject.getCode())
                .name(subject.getName() == null || subject.getName().isBlank()
                    ? subject.getDescription()
                    : subject.getName())
                .units(subject.getUnits())
                .type(subject.getType())
                .prerequisite(subject.getPrerequisite())
                .course(subject.getCourse())
                .yearLevel(subject.getYearLevel())
                .semester(subject.getSemester())
                .description(subject.getDescription())
                .status(subject.getStatus())
                .build();
    }
}
