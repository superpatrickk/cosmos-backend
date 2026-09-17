package com.pup.bataan.cosmos.repository;

import com.pup.bataan.cosmos.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findAllByOrderByNameAsc();

    List<Room> findAllByOrderByIdAsc();

        List<Room> findByNameContainingIgnoreCaseOrBuildingContainingIgnoreCaseOrCodeContainingIgnoreCase(
                String name,
                String building,
                String code
            );
}
