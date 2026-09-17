package com.pup.bataan.cosmos.service;

import com.pup.bataan.cosmos.dto.request.CreateFacultyMemberRequest;
import com.pup.bataan.cosmos.dto.request.UpdateFacultyMemberRequest;
import com.pup.bataan.cosmos.dto.response.FacultyMemberResponse;
import com.pup.bataan.cosmos.entity.FacultyMember;
import com.pup.bataan.cosmos.entity.FacultyStatus;
import com.pup.bataan.cosmos.exception.ResourceNotFoundException;
import com.pup.bataan.cosmos.repository.FacultyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FacultyService {

    private final FacultyMemberRepository facultyMemberRepository;

    public List<FacultyMemberResponse> getAll(String search) {
        List<FacultyMember> facultyMembers = (search == null || search.isBlank())
                ? facultyMemberRepository.findAllByOrderByNameAsc()
                : facultyMemberRepository.findByNameContainingIgnoreCaseOrDepartmentContainingIgnoreCaseOrEmailContainingIgnoreCaseOrSpecializationContainingIgnoreCase(
                        search, search, search, search
                );

        return facultyMembers.stream()
                .map(this::toResponse)
                .toList();
    }

    public FacultyMemberResponse getById(Long id) {
        FacultyMember facultyMember = findEntityById(id);
        return toResponse(facultyMember);
    }

    @Transactional
    public FacultyMemberResponse create(CreateFacultyMemberRequest request) {
        if (facultyMemberRepository.findByEmail(request.getEmail().trim().toLowerCase()).isPresent()) {
            throw new DataIntegrityViolationException("A faculty member with this email already exists.");
        }

        FacultyMember facultyMember = FacultyMember.builder()
                .name(request.getName().trim())
                .department(request.getDepartment().trim())
                .email(request.getEmail().trim().toLowerCase(Locale.ROOT))
                .phone(request.getPhone().trim())
                .specialization(request.getSpecialization().trim())
                .status(parseStatus(request.getStatus()))
                .build();

        return toResponse(facultyMemberRepository.save(facultyMember));
    }

    @Transactional
    public FacultyMemberResponse update(Long id, UpdateFacultyMemberRequest request) {
        FacultyMember facultyMember = findEntityById(id);

        if (request.getName() != null && !request.getName().isBlank()) {
            facultyMember.setName(request.getName().trim());
        }
        if (request.getDepartment() != null && !request.getDepartment().isBlank()) {
            facultyMember.setDepartment(request.getDepartment().trim());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);
            if (!normalizedEmail.equals(facultyMember.getEmail())
                    && facultyMemberRepository.findByEmail(normalizedEmail).isPresent()) {
                throw new DataIntegrityViolationException("A faculty member with this email already exists.");
            }
            facultyMember.setEmail(normalizedEmail);
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            facultyMember.setPhone(request.getPhone().trim());
        }
        if (request.getSpecialization() != null && !request.getSpecialization().isBlank()) {
            facultyMember.setSpecialization(request.getSpecialization().trim());
        }
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            facultyMember.setStatus(parseStatus(request.getStatus()));
        }

        return toResponse(facultyMemberRepository.save(facultyMember));
    }

    @Transactional
    public void delete(Long id) {
        FacultyMember facultyMember = findEntityById(id);
        facultyMemberRepository.delete(facultyMember);
    }

    private FacultyMember findEntityById(Long id) {
        return facultyMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty member not found with id: " + id));
    }

    private FacultyMemberResponse toResponse(FacultyMember facultyMember) {
        return FacultyMemberResponse.builder()
                .id(facultyMember.getId())
                .name(facultyMember.getName())
                .department(facultyMember.getDepartment())
                .email(facultyMember.getEmail())
                .phone(facultyMember.getPhone())
                .specialization(facultyMember.getSpecialization())
                .status(formatStatus(facultyMember.getStatus()))
                .build();
    }

    private FacultyStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return FacultyStatus.ACTIVE;
        }

        return switch (status.trim().toLowerCase(Locale.ROOT)) {
            case "active" -> FacultyStatus.ACTIVE;
            case "on leave", "on_leave", "onleave" -> FacultyStatus.ON_LEAVE;
            case "inactive" -> FacultyStatus.INACTIVE;
            default -> FacultyStatus.ACTIVE;
        };
    }

    private String formatStatus(FacultyStatus status) {
        return switch (status) {
            case ACTIVE -> "Active";
            case ON_LEAVE -> "On Leave";
            case INACTIVE -> "Inactive";
        };
    }
}
