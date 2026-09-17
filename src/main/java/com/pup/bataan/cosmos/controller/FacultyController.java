package com.pup.bataan.cosmos.controller;

import com.pup.bataan.cosmos.dto.request.CreateFacultyMemberRequest;
import com.pup.bataan.cosmos.dto.request.UpdateFacultyMemberRequest;
import com.pup.bataan.cosmos.dto.response.FacultyMemberResponse;
import com.pup.bataan.cosmos.service.FacultyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faculty")
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;

    @GetMapping
    public ResponseEntity<List<FacultyMemberResponse>> getAll(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(facultyService.getAll(search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacultyMemberResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(facultyService.getById(id));
    }

    @PostMapping
    public ResponseEntity<FacultyMemberResponse> create(@Valid @RequestBody CreateFacultyMemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facultyService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacultyMemberResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateFacultyMemberRequest request) {
        return ResponseEntity.ok(facultyService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        facultyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
