-- ============================================================
-- TẠO DATABASE
-- ============================================================
DROP DATABASE IF EXISTS learningenglish;
CREATE DATABASE learningenglish CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE learningenglish;

-- ============================================================
-- TẠO BẢNG
-- ============================================================

CREATE TABLE category_types (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE categories (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(150) NOT NULL,
    category_type_id BIGINT NOT NULL,
    created_at       DATETIME,
    updated_at       DATETIME,
    UNIQUE (category_type_id, name),
    FOREIGN KEY (category_type_id) REFERENCES category_types(id)
);

CREATE TABLE lesson_types (
    id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(100) NOT NULL,
    skill ENUM('READING','LISTENING','WRITING','SPEAKING') NOT NULL,
    UNIQUE (name, skill)
);

CREATE TABLE section_types (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(100) NOT NULL UNIQUE,
    save_type ENUM('AUTO','MANUAL') NOT NULL DEFAULT 'AUTO'
);

CREATE TABLE lessons (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    title          VARCHAR(255) NOT NULL,
    image_url      VARCHAR(1024),
    content        LONGTEXT,
    category_id    BIGINT NOT NULL,
    lesson_type_id BIGINT NOT NULL,
    created_at     DATETIME,
    updated_at     DATETIME,
    FOREIGN KEY (category_id)    REFERENCES categories(id),
    FOREIGN KEY (lesson_type_id) REFERENCES lesson_types(id)
);

CREATE TABLE sections (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    position        INT NOT NULL,
    content         JSON,
    question        JSON,
    options         JSON,
    answer          JSON,
    correct_answer  JSON,
    lesson_id       BIGINT NOT NULL,
    section_type_id BIGINT NOT NULL,
    created_at      DATETIME,
    updated_at      DATETIME,
    UNIQUE (lesson_id, position),
    FOREIGN KEY (lesson_id)       REFERENCES lessons(id),
    FOREIGN KEY (section_type_id) REFERENCES section_types(id)
);

CREATE TABLE users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    firstname     VARCHAR(100),
    lastname      VARCHAR(100),
    username      VARCHAR(255) NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER',
    avatar_url    VARCHAR(1024),
    created_at    DATETIME,
    updated_at    DATETIME
);

CREATE TABLE vip_packages (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(150) NOT NULL UNIQUE,
    description TEXT,
    months      INT NOT NULL,
    price       DECIMAL(10,2) NOT NULL,
    is_active   TINYINT(1) NOT NULL DEFAULT 1
);

CREATE TABLE payments (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount           DECIMAL(10,2) NOT NULL,
    months           INT,
    payment_method   ENUM('CARD','BANK_TRANSFER','MOMO','ZALOPAY','PAYPAL','OTHER') NOT NULL DEFAULT 'OTHER',
    transaction_code VARCHAR(255) NOT NULL UNIQUE,
    status           ENUM('PENDING','PAID','FAILED','REFUNDED','CANCELLED') NOT NULL DEFAULT 'PENDING',
    user_id          BIGINT NOT NULL,
    created_at       DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE user_vips (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_at       DATETIME NOT NULL,
    expire_at      DATETIME NOT NULL,
    user_id        BIGINT NOT NULL,
    vip_package_id BIGINT NOT NULL,
    payment_id     BIGINT,
    FOREIGN KEY (user_id)        REFERENCES users(id),
    FOREIGN KEY (vip_package_id) REFERENCES vip_packages(id),
    FOREIGN KEY (payment_id)     REFERENCES payments(id)
);

CREATE TABLE vocabularies (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    word       VARCHAR(120) NOT NULL,
    meaning    TEXT,
    example    TEXT,
    note       TEXT,
    user_id    BIGINT NOT NULL,
    created_at DATETIME,
    updated_at DATETIME,
    UNIQUE (user_id, word),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE study_plans (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    duration_days INT NOT NULL,
    goal_score    DECIMAL(4,2),
    user_id       BIGINT NOT NULL,
    created_at    DATETIME,
    updated_at    DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE progress_trackers (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    skill         ENUM('READING','LISTENING','WRITING','SPEAKING') NOT NULL,
    score         DECIMAL(5,2) NOT NULL DEFAULT 0,
    user_id       BIGINT NOT NULL,
    study_plan_id BIGINT NOT NULL,
    created_at    DATETIME,
    updated_at    DATETIME,
    UNIQUE (user_id, study_plan_id, skill),
    FOREIGN KEY (user_id)       REFERENCES users(id),
    FOREIGN KEY (study_plan_id) REFERENCES study_plans(id)
);

CREATE TABLE practice_sessions (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_at         DATETIME NOT NULL,
    duration_seconds INT NOT NULL DEFAULT 0,
    score            DECIMAL(5,2) NOT NULL DEFAULT 0,
    feedback         TEXT,
    user_id          BIGINT NOT NULL,
    lesson_id        BIGINT NOT NULL,
    created_at       DATETIME,
    updated_at       DATETIME,
    FOREIGN KEY (user_id)   REFERENCES users(id),
    FOREIGN KEY (lesson_id) REFERENCES lessons(id)
);

CREATE TABLE user_answers (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    score      DECIMAL(5,2) NOT NULL DEFAULT 0,
    session_id BIGINT NOT NULL,
    section_id BIGINT NOT NULL,
    created_at DATETIME,
    updated_at DATETIME,
    UNIQUE (session_id, section_id),
    FOREIGN KEY (session_id) REFERENCES practice_sessions(id),
    FOREIGN KEY (section_id) REFERENCES sections(id)
);

CREATE TABLE user_writing_answers (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    content          LONGTEXT NOT NULL,
    feedback         LONGTEXT,
    task_score       DECIMAL(5,2),
    coherence_score  DECIMAL(5,2),
    lexical_score    DECIMAL(5,2),
    grammar_score    DECIMAL(5,2),
    overall_score    DECIMAL(5,2),
    duration_seconds INT NOT NULL DEFAULT 0,
    user_id          BIGINT NOT NULL,
    lesson_id        BIGINT NOT NULL,
    created_at       DATETIME,
    updated_at       DATETIME,
    FOREIGN KEY (user_id)   REFERENCES users(id),
    FOREIGN KEY (lesson_id) REFERENCES lessons(id)
);

CREATE TABLE user_speaking_answers (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    audio_url          VARCHAR(2048) NOT NULL,
    transcript         LONGTEXT,
    feedback           LONGTEXT,
    pronunciation_score DECIMAL(5,2),
    fluency_score      DECIMAL(5,2),
    coherence_score    DECIMAL(5,2),
    lexical_score      DECIMAL(5,2),
    grammar_score      DECIMAL(5,2),
    overall_score      DECIMAL(5,2),
    duration_seconds   INT NOT NULL DEFAULT 0,
    user_id            BIGINT NOT NULL,
    lesson_id          BIGINT NOT NULL,
    created_at         DATETIME,
    updated_at         DATETIME,
    FOREIGN KEY (user_id)   REFERENCES users(id),
    FOREIGN KEY (lesson_id) REFERENCES lessons(id)
);

-- ============================================================
-- DATA MẪU
-- ============================================================

-- category_types
INSERT INTO category_types (name) VALUES
('Chủ đề giao tiếp'),
('Ngữ pháp'),
('Kỳ thi');

-- categories
INSERT INTO categories (name, category_type_id, created_at, updated_at) VALUES
('Du lịch',      1, NOW(), NOW()),
('Công việc',    1, NOW(), NOW()),
('Gia đình',     1, NOW(), NOW()),
('Thì hiện tại', 2, NOW(), NOW()),
('Thì quá khứ',  2, NOW(), NOW()),
('TOEIC',        3, NOW(), NOW()),
('IELTS',        3, NOW(), NOW());

-- lesson_types
INSERT INTO lesson_types (name, skill) VALUES
('Đọc hiểu',  'READING'),
('Nghe hiểu', 'LISTENING'),
('Viết luận', 'WRITING'),
('Luyện nói', 'SPEAKING');

-- section_types
INSERT INTO section_types (name, save_type) VALUES
('Lý thuyết',            'AUTO'),
('Trắc nghiệm',          'AUTO'),
('Điền vào chỗ trống',   'AUTO'),
('Tự luận',              'MANUAL'),
('Ghi âm',               'MANUAL');

-- lessons
INSERT INTO lessons (title, content, category_id, lesson_type_id, created_at, updated_at) VALUES
('Đặt phòng khách sạn',   'Học cách đặt phòng khách sạn bằng tiếng Anh.',         1, 1, NOW(), NOW()),
('Tại sân bay',            'Các mẫu câu thông dụng khi đi qua sân bay quốc tế.',   1, 2, NOW(), NOW()),
('Phỏng vấn xin việc',    'Luyện tập trả lời câu hỏi phỏng vấn tiếng Anh.',        2, 4, NOW(), NOW()),
('Viết email công việc',  'Cách viết email chuyên nghiệp bằng tiếng Anh.',          2, 3, NOW(), NOW()),
('Giới thiệu bản thân',   'Giới thiệu gia đình và bản thân bằng tiếng Anh.',        3, 4, NOW(), NOW()),
('Thì hiện tại đơn',      'Lý thuyết và bài tập thì hiện tại đơn.',                4, 1, NOW(), NOW()),
('Thì quá khứ đơn',       'Lý thuyết và bài tập thì quá khứ đơn.',                 5, 1, NOW(), NOW()),
('TOEIC Listening Part 1','Luyện nghe mô tả hình ảnh trong đề thi TOEIC.',         6, 2, NOW(), NOW()),
('IELTS Writing Task 2',  'Hướng dẫn viết bài luận IELTS Writing Task 2.',         7, 3, NOW(), NOW());

-- sections - Lesson 1: Đặt phòng khách sạn
INSERT INTO sections (position, content, question, options, answer, correct_answer, lesson_id, section_type_id, created_at, updated_at) VALUES
(1, '{"text":"Đọc hội thoại: A: I would like to book a room for two nights. B: Sure, single or double? A: Double, please."}', NULL, NULL, NULL, NULL, 1, 1, NOW(), NOW()),
(2, NULL, '{"text":"What type of room does the guest want?"}', '{"A":"Single","B":"Double","C":"Suite","D":"Twin"}', NULL, '{"value":"B"}', 1, 2, NOW(), NOW()),
(3, NULL, '{"text":"The guest wants to stay for ___ nights."}', NULL, NULL, '{"value":"two"}', 1, 3, NOW(), NOW());

-- sections - Lesson 6: Thì hiện tại đơn
INSERT INTO sections (position, content, question, options, answer, correct_answer, lesson_id, section_type_id, created_at, updated_at) VALUES
(1, '{"text":"Thì hiện tại đơn dùng để diễn tả hành động lặp lại, thói quen. Công thức: S + V(s/es)"}', NULL, NULL, NULL, NULL, 6, 1, NOW(), NOW()),
(2, NULL, '{"text":"She ___ to school every day."}', '{"A":"go","B":"goes","C":"going","D":"gone"}', NULL, '{"value":"B"}', 6, 2, NOW(), NOW()),
(3, NULL, '{"text":"Viết câu hoàn chỉnh: (I / eat / breakfast / every morning)"}', NULL, NULL, '{"value":"I eat breakfast every morning."}', 6, 4, NOW(), NOW());

-- sections - Lesson 8: TOEIC Listening Part 1
INSERT INTO sections (position, content, question, options, answer, correct_answer, lesson_id, section_type_id, created_at, updated_at) VALUES
(1, '{"text":"Part 1 TOEIC: Nghe một câu mô tả hình ảnh, chọn câu đúng nhất."}', NULL, NULL, NULL, NULL, 8, 1, NOW(), NOW()),
(2, NULL, '{"text":"Nghe và chọn câu mô tả đúng hình ảnh."}', '{"A":"The man is sitting at a desk.","B":"The woman is standing near a window.","C":"Two people are shaking hands.","D":"A child is reading a book."}', NULL, '{"value":"C"}', 8, 2, NOW(), NOW());

-- users (password: 123 được hash bcrypt)
INSERT INTO users (firstname, lastname, username, email, password_hash, role, created_at, updated_at) VALUES
('Admin', 'System', 'admin', 'admin@learningenglish.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'ADMIN', NOW(), NOW()),
('Nguyen', 'Van A', 'user1', 'user1@gmail.com',           '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'USER',  NOW(), NOW()),
('Tran', 'Thi B',   'user2', 'user2@gmail.com',           '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh7y', 'USER',  NOW(), NOW());

-- vip_packages
INSERT INTO vip_packages (name, description, months, price, is_active) VALUES
('VIP 1 tháng',  'Truy cập toàn bộ nội dung trong 1 tháng.',  1,  99000.00, 1),
('VIP 3 tháng',  'Truy cập toàn bộ nội dung trong 3 tháng.',  3, 249000.00, 1),
('VIP 12 tháng', 'Truy cập toàn bộ nội dung trong 12 tháng.', 12, 799000.00, 1);
