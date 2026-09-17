package com.pup.bataan.cosmos.controller;

import com.pup.bataan.cosmos.dto.request.CreateSubjectRequest;
import com.pup.bataan.cosmos.dto.request.UpdateSubjectRequest;
import com.pup.bataan.cosmos.dto.response.SubjectResponse;
import com.pup.bataan.cosmos.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    public ResponseEntity<List<SubjectResponse>> getAll(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(subjectService.getAll(search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(subjectService.getById(id));
    }

    @PostMapping
    public ResponseEntity<SubjectResponse> create(@Valid @RequestBody CreateSubjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subjectService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubjectResponse> update(@PathVariable Long id, @RequestBody UpdateSubjectRequest request) {
        return ResponseEntity.ok(subjectService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        subjectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
