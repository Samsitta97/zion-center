-- Drop the SET NULL foreign key constraint if it still exists
SET @fk_exists = (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'lessons'
      AND CONSTRAINT_NAME = 'fk_lesson_category'
      AND CONSTRAINT_TYPE = 'FOREIGN KEY'
);
SET @drop_stmt = IF(@fk_exists > 0,
    'ALTER TABLE lessons DROP FOREIGN KEY fk_lesson_category',
    'SELECT 1');
PREPARE stmt FROM @drop_stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Assign any NULL category_id rows to the first available category
UPDATE lessons
SET category_id = (SELECT id FROM categories ORDER BY id LIMIT 1)
WHERE category_id IS NULL;

-- Make the column NOT NULL
ALTER TABLE lessons MODIFY COLUMN category_id BIGINT NOT NULL;

-- Re-add the foreign key (RESTRICT by default) only if it doesn't exist
SET @fk_missing = (
    SELECT COUNT(*) = 0
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'lessons'
      AND CONSTRAINT_NAME = 'fk_lesson_category'
      AND CONSTRAINT_TYPE = 'FOREIGN KEY'
);
SET @add_stmt = IF(@fk_missing,
    'ALTER TABLE lessons ADD CONSTRAINT fk_lesson_category FOREIGN KEY (category_id) REFERENCES categories(id)',
    'SELECT 1');
PREPARE stmt FROM @add_stmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
