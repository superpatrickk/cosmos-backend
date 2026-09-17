package com.pup.bataan.cosmos;

import com.pup.bataan.cosmos.entity.Admin;
import com.pup.bataan.cosmos.entity.Subject;
import com.pup.bataan.cosmos.repository.AdminRepository;
import com.pup.bataan.cosmos.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final SubjectRepository subjectRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!adminRepository.existsByUsername("admin")) {
            Admin admin = Admin.builder()
                    .username("admin")
                    .email("admin@pup.edu.ph")
                    .fullName("Admin User")
                    .password(passwordEncoder.encode("Admin123"))
                    .role(Admin.Role.ADMIN)
                    .enabled(true)
                    .build();
            adminRepository.save(admin);
            log.info("Default admin account created.");
        }

        if (subjectRepository.count() == 0) {
            subjectRepository.save(Subject.builder()
                    .code("CS101")
                    .units(3)
                    .type("Lecture")
                    .prerequisite("None")
                    .course("BSCS")
                    .yearLevel("1st Year")
                    .semester("1st Semester")
                    .description("Fundamental programming concepts using Python and problem-solving techniques.")
                    .build());
            subjectRepository.save(Subject.builder()
                    .code("MATH101")
                    .units(3)
                    .type("Lecture")
                    .prerequisite("None")
                    .course("BSCS")
                    .yearLevel("1st Year")
                    .semester("1st Semester")
                    .description("Introduction to differential and integral calculus with applications.")
                    .build());
            subjectRepository.save(Subject.builder()
                    .code("CS201")
                    .units(3)
                    .type("Lecture/Lab")
                    .prerequisite("CS101")
                    .course("BSCS")
                    .yearLevel("2nd Year")
                    .semester("1st Semester")
                    .description("Study of data organization, manipulation, and algorithm efficiency.")
                    .build());
            subjectRepository.save(Subject.builder()
                    .code("CS301")
                    .units(3)
                    .type("Lecture/Lab")
                    .prerequisite("CS201")
                    .course("BSCS")
                    .yearLevel("3rd Year")
                    .semester("1st Semester")
                    .description("Relational databases, SQL, normalization, and database design principles.")
                    .build());
            subjectRepository.save(Subject.builder()
                    .code("EE101")
                    .units(3)
                    .type("Lecture")
                    .prerequisite("None")
                    .course("BSEE")
                    .yearLevel("1st Year")
                    .semester("1st Semester")
                    .description("Introduction to electrical engineering principles and circuits.")
                    .build());
            subjectRepository.save(Subject.builder()
                    .code("EE201")
                    .units(3)
                    .type("Lecture/Lab")
                    .prerequisite("MATH101")
                    .course("BSEE")
                    .yearLevel("2nd Year")
                    .semester("1st Semester")
                    .description("Analysis of DC and AC electrical circuits using standard techniques.")
                    .build());
            subjectRepository.save(Subject.builder()
                    .code("BA101")
                    .units(3)
                    .type("Lecture")
                    .prerequisite("None")
                    .course("BSBA")
                    .yearLevel("1st Year")
                    .semester("2nd Semester")
                    .description("Introduction to management theory and organizational behavior.")
                    .build());
            subjectRepository.save(Subject.builder()
                    .code("IT201")
                    .units(3)
                    .type("Lecture/Lab")
                    .prerequisite("CS101")
                    .course("BSIT")
                    .yearLevel("2nd Year")
                    .semester("2nd Semester")
                    .description("Frontend and backend web development using modern frameworks.")
                    .build());
            subjectRepository.save(Subject.builder()
                    .code("MATH201")
                    .units(3)
                    .type("Lecture")
                    .prerequisite("MATH101")
                    .course("BSCS")
                    .yearLevel("2nd Year")
                    .semester("2nd Semester")
                    .description("Continuation of Calculus I with multivariable and integral techniques.")
                    .build());
            log.info("Default subject catalog seeded.");
        }
    }
}