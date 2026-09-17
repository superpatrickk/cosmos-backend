package com.pup.bataan.cosmos.repository;

import com.pup.bataan.cosmos.entity.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {
    List<Curriculum> findByProgram(String program);
    List<Curriculum> findByAcademicYear(String academicYear);
    List<Curriculum> findByProgramAndYearLevelAndSemesterAndAcademicYear(
        String program, String yearLevel, String semester, String academicYear
    );
    List<Curriculum> findAllByOrderByProgramAscYearLevelAscSemesterAsc();
}
