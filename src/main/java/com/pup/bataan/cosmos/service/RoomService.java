package com.pup.bataan.cosmos.service;

import com.pup.bataan.cosmos.dto.request.CreateRoomRequest;
import com.pup.bataan.cosmos.dto.request.UpdateRoomRequest;
import com.pup.bataan.cosmos.dto.response.RoomResponse;
import com.pup.bataan.cosmos.entity.Room;
import com.pup.bataan.cosmos.entity.RoomStatus;
import com.pup.bataan.cosmos.exception.ResourceNotFoundException;
import com.pup.bataan.cosmos.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;

    public List<RoomResponse> getAll(String search, String building, String type, String status) {
        List<Room> rooms = (search == null || search.isBlank())
            ? roomRepository.findAllByOrderByIdAsc()
            : roomRepository.findByNameContainingIgnoreCaseOrBuildingContainingIgnoreCaseOrCodeContainingIgnoreCase(search, search, search);

        return rooms.stream()
                .filter(room -> matchesFilter(room, building, type, status))
                .map(this::toResponse)
                .toList();
    }

    public RoomResponse getById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public RoomResponse create(CreateRoomRequest request) {
        Room room = Room.builder()
                .name(request.getName().trim())
            .code(request.getCode().trim())
                .building(request.getBuilding().trim())
                .floor(request.getFloor().trim())
                .capacity(request.getCapacity())
                // type removed from DB
                .status(parseStatus(request.getStatus()))
                .build();

        return toResponse(roomRepository.save(room));
    }

    @Transactional
    public RoomResponse update(Long id, UpdateRoomRequest request) {
        Room room = findEntityById(id);
        if (request.getName() != null && !request.getName().isBlank()) room.setName(request.getName().trim());
        if (request.getName() != null && !request.getName().isBlank()) room.setName(request.getName().trim());
        if (request.getCode() != null && !request.getCode().isBlank()) room.setCode(request.getCode().trim());
        if (request.getBuilding() != null && !request.getBuilding().isBlank()) room.setBuilding(request.getBuilding().trim());
        if (request.getFloor() != null && !request.getFloor().isBlank()) room.setFloor(request.getFloor().trim());
        if (request.getCapacity() != null) room.setCapacity(request.getCapacity());
        // type removed from DB
        if (request.getStatus() != null && !request.getStatus().isBlank()) room.setStatus(parseStatus(request.getStatus()));
        // description and amenities removed — nothing to update
        return toResponse(roomRepository.save(room));
    }

    @Transactional
    public void delete(Long id) {
        roomRepository.delete(findEntityById(id));
    }

    private boolean matchesFilter(Room room, String building, String type, String status) {
        if (building != null && !building.isBlank() && !"All Buildings".equalsIgnoreCase(building) && !building.equalsIgnoreCase(room.getBuilding())) {
            return false;
        }
        // type filter removed — DB no longer contains `type`
        if (status != null && !status.isBlank() && !"All Status".equalsIgnoreCase(status) && !status.equalsIgnoreCase(formatStatus(room.getStatus()))) {
            return false;
        }
        return true;
    }

    private Room findEntityById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
    }

    private RoomResponse toResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .code((room.getCode() != null && !room.getCode().isBlank()) ? room.getCode() : (room.getId() != null ? "R" + room.getId() : ""))
                .name(room.getName())
                .building(room.getBuilding())
                .floor(room.getFloor())
                .capacity(room.getCapacity())
                .status(formatStatus(room.getStatus()))
                // description and amenities removed
                .build();
    }

    private RoomStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return RoomStatus.AVAILABLE;
        }
        return switch (status.trim().toLowerCase(Locale.ROOT)) {
            case "occupied" -> RoomStatus.OCCUPIED;
            case "maintenance" -> RoomStatus.MAINTENANCE;
            default -> RoomStatus.AVAILABLE;
        };
    }

    private String formatStatus(RoomStatus status) {
        return switch (status) {
            case AVAILABLE -> "Available";
            case OCCUPIED -> "Occupied";
            case MAINTENANCE -> "Maintenance";
        };
    }

        public com.pup.bataan.cosmos.dto.response.RoomMetadataResponse getMetadata() {
        List<Room> rooms = roomRepository.findAllByOrderByNameAsc();

        List<String> buildings = rooms.stream()
            .map(Room::getBuilding)
            .filter(s -> s != null && !s.isBlank())
            .distinct()
            .sorted()
            .toList();

        List<String> floors = rooms.stream()
            .map(Room::getFloor)
            .filter(s -> s != null && !s.isBlank())
            .distinct()
            .sorted()
            .toList();

        // type column removed from DB; no types metadata to return
        List<String> types = List.of();

        List<String> statuses = Arrays.stream(RoomStatus.values())
            .map(this::formatStatus)
            .toList();

        return com.pup.bataan.cosmos.dto.response.RoomMetadataResponse.builder()
            .buildings(buildings)
            .floors(floors)
            .types(types)
            .statuses(statuses)
            .build();
        }
}
