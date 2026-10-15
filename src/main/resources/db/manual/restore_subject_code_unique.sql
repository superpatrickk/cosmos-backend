SET @subject_code_unique_index = (
    SELECT INDEX_NAME
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'subjects'
      AND NON_UNIQUE = 0
      AND INDEX_NAME <> 'PRIMARY'
    GROUP BY INDEX_NAME
    HAVING GROUP_CONCAT(COLUMN_NAME ORDER BY SEQ_IN_INDEX) = 'code'
    LIMIT 1
);

SET @add_subject_code_unique_index = IF(
    @subject_code_unique_index IS NOT NULL,
    'SELECT 1',
    'ALTER TABLE `subjects` ADD UNIQUE INDEX `uk_subjects_code` (`code`)'
);

PREPARE add_subject_code_unique_index_statement FROM @add_subject_code_unique_index;
EXECUTE add_subject_code_unique_index_statement;
DEALLOCATE PREPARE add_subject_code_unique_index_statement;