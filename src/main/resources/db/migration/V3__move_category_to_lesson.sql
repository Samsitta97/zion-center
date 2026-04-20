-- Step 1: Add category_id to lessons
ALTER TABLE lessons
    ADD COLUMN category_id BIGINT NULL,
    ADD CONSTRAINT fk_lesson_category
        FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL;

CREATE INDEX idx_lessons_category_id ON lessons(category_id);

-- Step 2: Dynamically resolve the auto-generated FK name on classes and drop it
SET @fk_name = (
    SELECT kcu.CONSTRAINT_NAME
    FROM information_schema.KEY_COLUMN_USAGE kcu
    JOIN information_schema.TABLE_CONSTRAINTS tc
        ON tc.CONSTRAINT_NAME = kcu.CONSTRAINT_NAME
        AND tc.TABLE_SCHEMA   = kcu.TABLE_SCHEMA
        AND tc.TABLE_NAME     = kcu.TABLE_NAME
    WHERE kcu.TABLE_SCHEMA  = DATABASE()
      AND kcu.TABLE_NAME    = 'classes'
      AND kcu.COLUMN_NAME   = 'category_id'
      AND tc.CONSTRAINT_TYPE = 'FOREIGN KEY'
    LIMIT 1
);

SET @drop_fk = CONCAT('ALTER TABLE classes DROP FOREIGN KEY `', @fk_name, '`');
PREPARE stmt FROM @drop_fk;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Step 3: Drop the column from classes
ALTER TABLE classes DROP COLUMN category_id;
