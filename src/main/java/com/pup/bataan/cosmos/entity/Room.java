package com.pup.bataan.cosmos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_name", nullable = false, length = 100)
    private String name;

    @Column(name = "room_code", nullable = false, length = 50, unique = true)
    private String code;

    @Column(nullable = false, length = 100)
    private String building;

    @Column(nullable = false, length = 50)
    private String floor;

    @Column(nullable = false)
    private Integer capacity;

    // `type` column removed from DB
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private RoomStatus status = RoomStatus.AVAILABLE;

    @PrePersist
    @PreUpdate
    private void normalize() {
        if (name != null) {
            name = name.trim();
        }
        if (building != null) {
            building = building.trim();
        }
        if (code != null) {
            code = code.trim();
        }
        if (floor != null) {
            floor = floor.trim();
        }
        // type column removed from DB
        // description and amenities columns removed from DB; nothing to normalize here
        if (status == null) {
            status = RoomStatus.AVAILABLE;
        }
    }
}
