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

    private static final List<String> VALID_FACULTY_TYPES = List.of(
            "Core",
            "Allied",
            "Part-time",
            "GEED (Full-time)",
            "GEED (Part-time)"
    );

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
        String normalizedName = normalizeName(request.getName());
        String facultyType = normalizeFacultyType(request.getType());
        String email = normalizeOptional(request.getEmail());
        if (email != null && facultyMemberRepository.findByEmail(email).isPresent()) {
            throw new DataIntegrityViolationException("A faculty member with this email already exists.");
        }

        List<String> programs = normalizePrograms(request.getPrograms(), request.getDepartment(), facultyType);
        if (!isGEEDType(facultyType) && programs.isEmpty()) {
            throw new IllegalArgumentException("At least one program is required for non-GEED faculty.");
        }

        FacultyMember facultyMember = FacultyMember.builder()
                .name(normalizedName)
                .type(facultyType)
                .programs(programs)
                .department(isGEEDType(facultyType) ? "GEED" : String.join(", ", programs))
                .email(email != null ? email : "")
                .phone(normalizeOptional(request.getPhone()) != null ? normalizeOptional(request.getPhone()) : "")
                .specialization(request.getSpecialization().trim())
                .status(parseStatus(request.getStatus()))
                .build();

        return toResponse(facultyMemberRepository.save(facultyMember));
    }

    @Transactional
    public FacultyMemberResponse update(Long id, UpdateFacultyMemberRequest request) {
        FacultyMember facultyMember = findEntityById(id);

        if (request.getName() != null && !request.getName().isBlank()) {
            facultyMember.setName(normalizeName(request.getName()));
        }
        if (request.getType() != null && !request.getType().isBlank()) {
            String facultyType = normalizeFacultyType(request.getType());
            facultyMember.setType(facultyType);
        }
        if (request.getPrograms() != null || request.getDepartment() != null || request.getType() != null) {
            String facultyType = facultyMember.getType() != null ? facultyMember.getType() : "Core";
            List<String> programs = normalizePrograms(request.getPrograms(), request.getDepartment(), facultyType);
            if (!isGEEDType(facultyType) && programs.isEmpty()) {
                throw new IllegalArgumentException("At least one program is required for non-GEED faculty.");
            }
            facultyMember.setPrograms(programs);
            facultyMember.setDepartment(isGEEDType(facultyType) ? "GEED" : String.join(", ", programs));
        }
        if (request.getEmail() != null) {
            String normalizedEmail = normalizeOptional(request.getEmail());
            if (normalizedEmail != null && !normalizedEmail.equals(facultyMember.getEmail())
                    && facultyMemberRepository.findByEmail(normalizedEmail).isPresent()) {
                throw new DataIntegrityViolationException("A faculty member with this email already exists.");
            }
            facultyMember.setEmail(normalizedEmail != null ? normalizedEmail : "");
        }
        if (request.getPhone() != null) {
            facultyMember.setPhone(normalizeOptional(request.getPhone()) != null ? normalizeOptional(request.getPhone()) : "");
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
                .type(facultyMember.getType())
                .programs(facultyMember.getPrograms())
                .department(facultyMember.getDepartment())
                .email(facultyMember.getEmail())
                .phone(facultyMember.getPhone())
                .specialization(facultyMember.getSpecialization())
                .status(formatStatus(facultyMember.getStatus()))
                .build();
    }

    private List<String> normalizePrograms(List<String> requestedPrograms, String legacyDepartment, String facultyType) {
        if (isGEEDType(facultyType)) {
            return List.of();
        }

        List<String> source = requestedPrograms;
        if ((source == null || source.isEmpty()) && legacyDepartment != null && !legacyDepartment.isBlank()) {
            source = List.of(legacyDepartment.split(","));
        }
        if (source == null) {
            return List.of();
        }
        return source.stream()
                .filter(program -> program != null && !program.isBlank())
                .map(program -> program.trim().toUpperCase(Locale.ROOT))
                .distinct()
                .toList();
    }

    private String normalizeName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Name is required.");
        }

        return value.trim();
    }

    private String normalizeFacultyType(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Faculty type is required.");
        }

        String normalized = value.trim();
        if (!VALID_FACULTY_TYPES.contains(normalized)) {
            throw new IllegalArgumentException("Invalid faculty type.");
        }

        return normalized;
    }

    private String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private boolean isGEEDType(String facultyType) {
        return "GEED (Full-time)".equals(facultyType) || "GEED (Part-time)".equals(facultyType);
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
