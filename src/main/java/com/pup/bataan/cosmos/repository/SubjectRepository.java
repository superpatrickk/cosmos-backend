package com.pup.bataan.cosmos.repository;

import com.pup.bataan.cosmos.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByCode(String code);
    boolean existsByCode(String code);
    List<Subject> findAllByOrderByCodeAsc();
        List<Subject> findByCodeContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrCourseContainingIgnoreCaseOrYearLevelContainingIgnoreCase(
            String code,
            String description,
            String course,
            String yearLevel
    );
}
