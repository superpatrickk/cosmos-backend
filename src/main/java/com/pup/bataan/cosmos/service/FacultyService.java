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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FacultyService {

    private static final List<String> VALID_FACULTY_TYPES = List.of(
            "Full Time Faculty Core",
            "Full Time Faculty Allied",
            "Part Time Faculty Core",
            "Part Time Faculty Allied",
            "Full Time Part time Faculty Core",
            "Full Time Part time Faculty Allied"
    );

    private final FacultyMemberRepository facultyMemberRepository;

    public List<FacultyMemberResponse> getAll(String search) {
        List<FacultyMember> facultyMembers = (search == null || search.isBlank())
                ? facultyMemberRepository.findAllByOrderByNameAsc()
                : facultyMemberRepository.findByNameContainingIgnoreCaseOrDepartmentContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        search, search, search
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

        List<String> programs = normalizePrograms(request.getPrograms(), request.getDepartment());
        if (programs.isEmpty()) {
            throw new IllegalArgumentException("At least one program is required.");
        }

        FacultyMember facultyMember = FacultyMember.builder()
                .name(normalizedName)
                .type(facultyType)
                .programs(programs)
                .department(String.join(", ", programs))
                .email(email)
                .phone(normalizeOptional(request.getPhone()) != null ? normalizeOptional(request.getPhone()) : "")
                .status(parseStatus(request.getStatus()))
                .build();

        return toResponse(facultyMemberRepository.save(facultyMember));
    }

    @Transactional
    public FacultyMemberResponse update(Long id, UpdateFacultyMemberRequest request) {
        FacultyMember facultyMember = findEntityById(id);

        if (request.getName() != null) {
            facultyMember.setName(normalizeName(request.getName()));
        }

        if (request.getType() != null && !request.getType().isBlank()) {
            facultyMember.setType(normalizeFacultyType(request.getType()));
        }

        if (request.getPrograms() != null || request.getDepartment() != null || request.getType() != null) {
            List<String> programs = normalizePrograms(request.getPrograms(), request.getDepartment());
            if (programs.isEmpty()) {
                throw new IllegalArgumentException("At least one program is required.");
            }
            facultyMember.getPrograms().clear();
            facultyMember.getPrograms().addAll(programs);
            facultyMember.setDepartment(String.join(", ", programs));
        }
        if (request.getEmail() != null) {
            String normalizedEmail = normalizeOptional(request.getEmail());
            if (normalizedEmail != null && !normalizedEmail.equals(facultyMember.getEmail())
                    && facultyMemberRepository.findByEmail(normalizedEmail).isPresent()) {
                throw new DataIntegrityViolationException("A faculty member with this email already exists.");
            }
            facultyMember.setEmail(normalizedEmail);
        }
        if (request.getPhone() != null) {
            facultyMember.setPhone(normalizeOptional(request.getPhone()) != null ? normalizeOptional(request.getPhone()) : "");
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
                .status(formatStatus(facultyMember.getStatus()))
                .build();
    }

    private List<String> normalizePrograms(List<String> requestedPrograms, String legacyDepartment) {
        List<String> source = requestedPrograms;
        if ((source == null || source.isEmpty()) && legacyDepartment != null && !legacyDepartment.isBlank()) {
            source = "GEED".equalsIgnoreCase(legacyDepartment.trim())
                    ? new ArrayList<>()
                    : List.of(legacyDepartment.split(","));
        }
        if (source == null) {
            return new ArrayList<>();
        }
        return source.stream()
                .filter(program -> program != null && !program.isBlank())
                .map(program -> program.trim().toUpperCase(Locale.ROOT))
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
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
        normalized = switch (normalized) {
            case "Core", "GEED (Full-time)", "FULL TIME FACULTY CORE" -> "Full Time Faculty Core";
            case "Allied", "FULL TIME FACULTY ALLIED" -> "Full Time Faculty Allied";
            case "FULL TIME PART TIME FACULTY CORE" -> "Full Time Part time Faculty Core";
            case "FULL TIME PART TIME FACULTY ALLIED" -> "Full Time Part time Faculty Allied";
            case "Part-time", "GEED (Part-time)", "PART TIME FACULTY CORE" -> "Part Time Faculty Core";
            case "PART TIME FACULTY ALLIED" -> "Part Time Faculty Allied";
            default -> normalized;
        };
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
