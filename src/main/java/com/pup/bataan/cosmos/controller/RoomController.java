package com.pup.bataan.cosmos.controller;

import com.pup.bataan.cosmos.dto.request.CreateRoomRequest;
import com.pup.bataan.cosmos.dto.request.UpdateRoomRequest;
import com.pup.bataan.cosmos.dto.response.RoomResponse;
import com.pup.bataan.cosmos.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String building,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(roomService.getAll(search, building, type, status));
    }

    @GetMapping("/meta")
    public ResponseEntity<com.pup.bataan.cosmos.dto.response.RoomMetadataResponse> getMetadata() {
        return ResponseEntity.ok(roomService.getMetadata());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getById(id));
    }

    @PostMapping
    public ResponseEntity<RoomResponse> create(@Valid @RequestBody CreateRoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateRoomRequest request) {
        return ResponseEntity.ok(roomService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
