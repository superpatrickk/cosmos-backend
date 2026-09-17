package com.pup.bataan.cosmos.repository;

import com.pup.bataan.cosmos.entity.FacultyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyMemberRepository extends JpaRepository<FacultyMember, Long> {

    Optional<FacultyMember> findByEmail(String email);

    List<FacultyMember> findAllByOrderByNameAsc();

    List<FacultyMember> findByNameContainingIgnoreCaseOrDepartmentContainingIgnoreCaseOrEmailContainingIgnoreCaseOrSpecializationContainingIgnoreCase(
            String name,
            String department,
            String email,
            String specialization
    );
}
