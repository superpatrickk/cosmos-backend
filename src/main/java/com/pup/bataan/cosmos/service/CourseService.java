package com.pup.bataan.cosmos.service;

import com.pup.bataan.cosmos.dto.request.CreateCourseRequest;
import com.pup.bataan.cosmos.dto.request.UpdateCourseRequest;
import com.pup.bataan.cosmos.dto.response.CourseResponse;
import com.pup.bataan.cosmos.entity.Course;
import com.pup.bataan.cosmos.entity.CourseStatus;
import com.pup.bataan.cosmos.exception.ResourceNotFoundException;
import com.pup.bataan.cosmos.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;

    public List<CourseResponse> getAll(String search) {
        List<Course> courses = (search == null || search.isBlank())
                ? courseRepository.findAllByOrderByNameAsc()
                : courseRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCaseOrDepartmentContainingIgnoreCase(search, search, search);

        return courses.stream().map(this::toResponse).toList();
    }

    public CourseResponse getById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public CourseResponse create(CreateCourseRequest request) {
        if (courseRepository.findByCode(request.getCode().trim().toUpperCase(Locale.ROOT)).isPresent()) {
            throw new DataIntegrityViolationException("A course with this code already exists.");
        }

        Course course = Course.builder()
                .code(request.getCode().trim().toUpperCase(Locale.ROOT))
                .name(request.getName().trim())
                .department(request.getDepartment().trim())
                .duration(request.getDuration().trim())
                .students(request.getStudents() != null ? request.getStudents() : 0)
                .status(parseStatus(request.getStatus()))
                .description(request.getDescription().trim())
                .yearEstablished(request.getYearEstablished() != null ? request.getYearEstablished() : 2000)
                .totalSections(request.getTotalSections() != null ? request.getTotalSections() : 0)
                .build();

        return toResponse(courseRepository.save(course));
    }

    @Transactional
    public CourseResponse update(Long id, UpdateCourseRequest request) {
        Course course = findEntityById(id);
        if (request.getCode() != null && !request.getCode().isBlank()) {
            String normalizedCode = request.getCode().trim().toUpperCase(Locale.ROOT);
            if (!normalizedCode.equals(course.getCode()) && courseRepository.findByCode(normalizedCode).isPresent()) {
                throw new DataIntegrityViolationException("A course with this code already exists.");
            }
            course.setCode(normalizedCode);
        }
        if (request.getName() != null && !request.getName().isBlank()) {
            course.setName(request.getName().trim());
        }
        if (request.getDepartment() != null && !request.getDepartment().isBlank()) {
            course.setDepartment(request.getDepartment().trim());
        }
        if (request.getDuration() != null && !request.getDuration().isBlank()) {
            course.setDuration(request.getDuration().trim());
        }
        if (request.getStudents() != null) {
            course.setStudents(request.getStudents());
        }
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            course.setStatus(parseStatus(request.getStatus()));
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            course.setDescription(request.getDescription().trim());
        }
        if (request.getYearEstablished() != null) {
            course.setYearEstablished(request.getYearEstablished());
        }
        if (request.getTotalSections() != null) {
            course.setTotalSections(request.getTotalSections());
        }
        return toResponse(courseRepository.save(course));
    }

    @Transactional
    public void delete(Long id) {
        Course course = findEntityById(id);
        courseRepository.delete(course);
    }

    private Course findEntityById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    private CourseResponse toResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .code(course.getCode())
                .name(course.getName())
                .department(course.getDepartment())
                .duration(course.getDuration())
                .students(course.getStudents())
                .status(formatStatus(course.getStatus()))
                .description(course.getDescription())
                .yearEstablished(course.getYearEstablished())
                .totalSections(course.getTotalSections())
                .build();
    }

    private CourseStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return CourseStatus.ACTIVE;
        }
        return switch (status.trim().toLowerCase(Locale.ROOT)) {
            case "inactive" -> CourseStatus.INACTIVE;
            default -> CourseStatus.ACTIVE;
        };
    }

    private String formatStatus(CourseStatus status) {
        return status == CourseStatus.ACTIVE ? "Active" : "Inactive";
    }
}
