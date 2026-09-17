package com.pup.bataan.cosmos.repository;

import com.pup.bataan.cosmos.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCode(String code);

    List<Course> findAllByOrderByNameAsc();

    List<Course> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
            String name,
            String code,
            String department
    );
}
