package com.pup.bataan.cosmos.controller;

import com.pup.bataan.cosmos.dto.request.CreateCurriculumRequest;
import com.pup.bataan.cosmos.dto.request.UpdateCurriculumRequest;
import com.pup.bataan.cosmos.dto.response.CurriculumResponse;
import com.pup.bataan.cosmos.dto.response.FacultyMemberResponse;
import com.pup.bataan.cosmos.service.CurriculumService;
import com.pup.bataan.cosmos.service.FacultyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/curriculum", "/api/curricula"})
@RequiredArgsConstructor
public class CurriculumController {

    private final CurriculumService curriculumService;
    private final FacultyService facultyService;

    @GetMapping
    public ResponseEntity<List<CurriculumResponse>> getAll() {
        return ResponseEntity.ok(curriculumService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CurriculumResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(curriculumService.getById(id));
    }

    @GetMapping("/by-program/{program}")
    public ResponseEntity<List<CurriculumResponse>> getByProgram(@PathVariable String program) {
        return ResponseEntity.ok(curriculumService.getByProgram(program));
    }

    @GetMapping("/by-academic-year/{academicYear}")
    public ResponseEntity<List<CurriculumResponse>> getByAcademicYear(@PathVariable String academicYear) {
        return ResponseEntity.ok(curriculumService.getByAcademicYear(academicYear));
    }

    @PostMapping
    public ResponseEntity<CurriculumResponse> create(@Valid @RequestBody CreateCurriculumRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(curriculumService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CurriculumResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateCurriculumRequest request) {
        return ResponseEntity.ok(curriculumService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        curriculumService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint to fetch all available faculty members for curriculum assignment
     */
    @GetMapping("/faculty/available")
    public ResponseEntity<List<FacultyMemberResponse>> getAvailableFaculty() {
        return ResponseEntity.ok(facultyService.getAll(null));
    }
}
