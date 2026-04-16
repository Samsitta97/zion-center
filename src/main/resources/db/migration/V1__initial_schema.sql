CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       is_active BOOLEAN DEFAULT TRUE,
                       role ENUM('ADMIN', 'TEACHER') DEFAULT 'TEACHER',
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- categories (for classifying classes by topic, book, series, etc.)
CREATE TABLE categories (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            description TEXT,
                            created_by BIGINT NOT NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

-- classes
CREATE TABLE classes (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         category_id BIGINT,
                         title VARCHAR(255) NOT NULL,
                         description TEXT,
                         class_date DATE,
                         status ENUM('draft', 'active', 'archived') DEFAULT 'active',
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                         FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- lessons (renamed from videos)
CREATE TABLE lessons (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         class_id BIGINT NOT NULL,
                         youtube_url VARCHAR(500) NOT NULL,
                         youtube_video_id VARCHAR(100),
                         title VARCHAR(255) NOT NULL,
                         description TEXT,
                         duration_seconds INT,
                         is_active BOOLEAN DEFAULT TRUE,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE
);

-- shared_links
CREATE TABLE shared_links (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              lesson_id BIGINT NOT NULL,
                              user_id BIGINT NOT NULL,
                              token VARCHAR(255) UNIQUE NOT NULL,
                              expires_at TIMESTAMP,
                              view_count INT DEFAULT 0,
                              max_views INT,
                              is_active BOOLEAN DEFAULT TRUE,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE CASCADE,
                              FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- link_access_logs
-- ip_address: internet address of the device that opened the link (e.g. 102.45.67.89)
-- user_agent: browser/device info sent automatically (e.g. Chrome on iPhone)

CREATE TABLE link_access_logs (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  shared_link_id BIGINT NOT NULL,
                                  ip_address VARCHAR(45),
                                  user_agent VARCHAR(500),
                                  accessed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  FOREIGN KEY (shared_link_id) REFERENCES shared_links(id) ON DELETE CASCADE
);

-- students (for future use when student login is enabled)

CREATE TABLE students (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          email VARCHAR(255) UNIQUE NOT NULL,
                          phone VARCHAR(50),
                          is_active BOOLEAN DEFAULT TRUE,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- enrollments (for future use: links students to classes)

CREATE TABLE enrollments (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             student_id BIGINT NOT NULL,
                             class_id BIGINT NOT NULL,
                             status ENUM('active', 'completed', 'dropped') DEFAULT 'active',
                             enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             UNIQUE KEY uq_student_class (student_id, class_id),
                             FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
                             FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE
);