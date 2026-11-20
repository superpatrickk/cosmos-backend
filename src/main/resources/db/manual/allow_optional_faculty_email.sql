ALTER TABLE faculty_members
    MODIFY COLUMN email VARCHAR(150) NULL;

UPDATE faculty_members
SET email = NULL
WHERE email = '';
