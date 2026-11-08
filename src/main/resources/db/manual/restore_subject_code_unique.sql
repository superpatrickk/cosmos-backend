DROP INDEX IF EXISTS uk_subjects_code ON subjects;
CREATE UNIQUE INDEX IF NOT EXISTS uk_subjects_course_code ON subjects (course, code);