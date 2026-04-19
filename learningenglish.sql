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

    payment_method   ENUM('CARD','BANK_TRANSFER','MOMO','ZALOPAY','PAYPAL','OTHER') NOT NULL DEFAULT 'OTHER',
    transaction_code VARCHAR(255) NOT NULL UNIQUE,
    status           ENUM('PENDING','PAID','FAILED','REFUNDED','CANCELLED') NOT NULL DEFAULT 'PENDING',

    user_id          BIGINT NOT NULL,
    vip_package_id   BIGINT NOT NULL,   -- ✅ THÊM DÒNG NÀY

    created_at       DATETIME,

    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (vip_package_id) REFERENCES vip_packages(id)
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
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    audio_url           VARCHAR(2048) NOT NULL,
    transcript          LONGTEXT,
    feedback            LONGTEXT,
    pronunciation_score DECIMAL(5,2),
    fluency_score       DECIMAL(5,2),
    coherence_score     DECIMAL(5,2),
    lexical_score       DECIMAL(5,2),
    grammar_score       DECIMAL(5,2),
    overall_score       DECIMAL(5,2),
    duration_seconds    INT NOT NULL DEFAULT 0,
    user_id             BIGINT NOT NULL,
    lesson_id           BIGINT NOT NULL,
    created_at          DATETIME,
    updated_at          DATETIME,
    FOREIGN KEY (user_id)   REFERENCES users(id),
    FOREIGN KEY (lesson_id) REFERENCES lessons(id)
);

-- ============================================================
-- DATA MAU — IELTS
-- ============================================================

INSERT INTO category_types (id, name) VALUES (1, 'IELTS');

INSERT INTO categories (id, name, category_type_id, created_at, updated_at) VALUES
(1, 'Technology',  1, NOW(), NOW()),
(2, 'Education',   1, NOW(), NOW()),
(3, 'Environment', 1, NOW(), NOW()),
(4, 'Health',      1, NOW(), NOW()),
(5, 'Society',     1, NOW(), NOW());

INSERT INTO lesson_types (id, name, skill) VALUES
(1, 'Reading Passage',    'READING'),
(2, 'Listening Practice', 'LISTENING'),
(3, 'Writing Task',       'WRITING'),
(4, 'Speaking Practice',  'SPEAKING');

INSERT INTO section_types (id, name, save_type) VALUES
(1, 'READING_PASSAGE', 'AUTO'),
(2, 'MULTIPLE_CHOICE', 'AUTO'),
(3, 'FILL_IN_BLANK',   'AUTO'),
(4, 'TRUE_FALSE_NG',   'AUTO'),
(5, 'LISTENING_AUDIO', 'AUTO'),
(6, 'WRITING_TASK',    'MANUAL'),
(7, 'SPEAKING_TASK',   'MANUAL');

-- ============================================================
-- LESSON 1: READING — Technology
-- ============================================================
INSERT INTO lessons (id, title, content, category_id, lesson_type_id, created_at, updated_at)
VALUES (1, 'IELTS Academic Reading — Artificial Intelligence and the Future of Work', '', 1, 1, NOW(), NOW());

INSERT INTO sections (lesson_id, position, section_type_id, content, created_at, updated_at) VALUES
(1, 1, 1, JSON_OBJECT(
    'title', 'Artificial Intelligence and the Future of Work',
    'text', 'Artificial Intelligence (AI) is rapidly transforming industries worldwide. From healthcare to transportation, AI-powered systems are automating tasks that once required human expertise. In the medical field, AI algorithms can now detect diseases from imaging scans with accuracy comparable to trained radiologists. In manufacturing, robotic systems guided by machine learning are assembling products faster and with fewer errors than human workers.\n\nHowever, the rise of AI has also raised significant concerns about employment. Economists predict that up to 30% of current jobs could be automated by 2030. While new roles will emerge, critics argue that the transition will disproportionately affect low-skilled workers who may lack the resources to retrain. Governments and educational institutions are being urged to invest in reskilling programmes to prepare the workforce for an AI-driven economy.\n\nDespite these challenges, proponents of AI argue that the technology will ultimately create more jobs than it displaces, much as previous industrial revolutions did. They point to the growth of entirely new industries — from data science to AI ethics — as evidence that technological advancement drives economic expansion. The key, they suggest, lies in ensuring that the benefits of AI are distributed equitably across society.'
), NOW(), NOW());

INSERT INTO sections (lesson_id, position, section_type_id, question, options, correct_answer, created_at, updated_at) VALUES
(1, 2, 2,
    JSON_OBJECT('text', 'According to the passage, what can AI algorithms do in the medical field?'),
    JSON_ARRAY('Perform surgical operations', 'Detect diseases from imaging scans', 'Replace all hospital staff', 'Prescribe medication to patients'),
    JSON_OBJECT('value', 'Detect diseases from imaging scans'),
    NOW(), NOW()),
(1, 3, 2,
    JSON_OBJECT('text', 'What percentage of jobs could be automated by 2030, according to economists?'),
    JSON_ARRAY('10%', '20%', '30%', '50%'),
    JSON_OBJECT('value', '30%'),
    NOW(), NOW()),
(1, 4, 2,
    JSON_OBJECT('text', 'Which field is NOT mentioned as being transformed by AI in the passage?'),
    JSON_ARRAY('Healthcare', 'Manufacturing', 'Transportation', 'Agriculture'),
    JSON_OBJECT('value', 'Agriculture'),
    NOW(), NOW());

INSERT INTO sections (lesson_id, position, section_type_id, question, options, correct_answer, created_at, updated_at) VALUES
(1, 5, 4,
    JSON_OBJECT('text', 'AI will definitely create more jobs than it replaces, according to all economists.'),
    JSON_ARRAY('TRUE', 'FALSE', 'NOT GIVEN'),
    JSON_OBJECT('value', 'NOT GIVEN'),
    NOW(), NOW()),
(1, 6, 4,
    JSON_OBJECT('text', 'Low-skilled workers are expected to be more affected by AI automation than high-skilled workers.'),
    JSON_ARRAY('TRUE', 'FALSE', 'NOT GIVEN'),
    JSON_OBJECT('value', 'TRUE'),
    NOW(), NOW());

INSERT INTO sections (lesson_id, position, section_type_id, question, correct_answer, created_at, updated_at) VALUES
(1, 7, 3,
    JSON_OBJECT('text', 'Governments are urged to invest in ______ programmes to prepare workers for an AI-driven economy.'),
    JSON_OBJECT('value', 'reskilling'),
    NOW(), NOW()),
(1, 8, 3,
    JSON_OBJECT('text', 'In manufacturing, ______ systems guided by machine learning assemble products with fewer errors.'),
    JSON_OBJECT('value', 'robotic'),
    NOW(), NOW());

-- ============================================================
-- LESSON 2: READING — Environment
-- ============================================================
INSERT INTO lessons (id, title, content, category_id, lesson_type_id, created_at, updated_at)
VALUES (2, 'IELTS Academic Reading — Climate Change and Urban Planning', '', 3, 1, NOW(), NOW());

INSERT INTO sections (lesson_id, position, section_type_id, content, created_at, updated_at) VALUES
(2, 1, 1, JSON_OBJECT(
    'title', 'Climate Change and Urban Planning',
    'text', 'Cities around the world are increasingly vulnerable to the effects of climate change. Rising sea levels, more frequent extreme weather events, and prolonged heatwaves pose significant threats to urban infrastructure and public health. In response, urban planners and policymakers are rethinking how cities are designed and managed.\n\nOne widely adopted strategy is the development of green infrastructure — incorporating parks, green roofs, and tree-lined streets into urban design. These elements help reduce the urban heat island effect, manage stormwater runoff, and improve air quality. Cities such as Singapore and Copenhagen have become global models for sustainable urban planning, investing heavily in green spaces and renewable energy systems.\n\nAnother approach is the construction of resilient infrastructure. This involves designing buildings, roads, and drainage systems that can withstand extreme weather conditions. Some coastal cities are also exploring more radical solutions, such as constructing sea walls or even relocating vulnerable communities to higher ground. While these measures are costly, proponents argue that the long-term economic and social benefits far outweigh the initial investment.'
), NOW(), NOW());

INSERT INTO sections (lesson_id, position, section_type_id, question, options, correct_answer, created_at, updated_at) VALUES
(2, 2, 2,
    JSON_OBJECT('text', 'Which city is mentioned as a global model for sustainable urban planning?'),
    JSON_ARRAY('Tokyo', 'Singapore', 'New York', 'London'),
    JSON_OBJECT('value', 'Singapore'),
    NOW(), NOW()),
(2, 3, 2,
    JSON_OBJECT('text', 'What does green infrastructure help to reduce?'),
    JSON_ARRAY('Carbon emissions from vehicles', 'The urban heat island effect', 'Noise pollution in city centres', 'Water consumption in buildings'),
    JSON_OBJECT('value', 'The urban heat island effect'),
    NOW(), NOW());

INSERT INTO sections (lesson_id, position, section_type_id, question, options, correct_answer, created_at, updated_at) VALUES
(2, 4, 4,
    JSON_OBJECT('text', 'All coastal cities have already built sea walls to protect against rising sea levels.'),
    JSON_ARRAY('TRUE', 'FALSE', 'NOT GIVEN'),
    JSON_OBJECT('value', 'NOT GIVEN'),
    NOW(), NOW()),
(2, 5, 4,
    JSON_OBJECT('text', 'Green roofs and parks help manage stormwater runoff in cities.'),
    JSON_ARRAY('TRUE', 'FALSE', 'NOT GIVEN'),
    JSON_OBJECT('value', 'TRUE'),
    NOW(), NOW());

INSERT INTO sections (lesson_id, position, section_type_id, question, correct_answer, created_at, updated_at) VALUES
(2, 6, 3,
    JSON_OBJECT('text', 'Urban planners incorporate parks and ______ roofs as part of green infrastructure.'),
    JSON_OBJECT('value', 'green'),
    NOW(), NOW()),
(2, 7, 3,
    JSON_OBJECT('text', 'Cities such as Singapore and ______ have become global models for sustainable urban planning.'),
    JSON_OBJECT('value', 'Copenhagen'),
    NOW(), NOW());

-- ============================================================
-- LESSON 3: LISTENING — Education
-- ============================================================
INSERT INTO lessons (id, title, content, category_id, lesson_type_id, created_at, updated_at)
VALUES (3, 'IELTS Listening — University Orientation Talk', '', 2, 2, NOW(), NOW());

INSERT INTO sections (lesson_id, position, section_type_id, content, created_at, updated_at) VALUES
(3, 1, 5, JSON_OBJECT(
    'audioUrl',    'https://example.com/audio/ielts-orientation.mp3',
    'duration',    '4:20',
    'instruction', 'You will hear a university officer giving an orientation talk to new students. Listen carefully and answer Questions 1-4.'
), NOW(), NOW());

INSERT INTO sections (lesson_id, position, section_type_id, question, options, correct_answer, created_at, updated_at) VALUES
(3, 2, 2,
    JSON_OBJECT('text', 'What time does the university library open on weekdays?'),
    JSON_ARRAY('7:00 AM', '8:00 AM', '9:00 AM', '10:00 AM'),
    JSON_OBJECT('value', '8:00 AM'),
    NOW(), NOW()),
(3, 3, 2,
    JSON_OBJECT('text', 'Where should students collect their student ID cards?'),
    JSON_ARRAY('The main office', 'The library', 'The student services centre', 'The IT department'),
    JSON_OBJECT('value', 'The student services centre'),
    NOW(), NOW());

INSERT INTO sections (lesson_id, position, section_type_id, question, correct_answer, created_at, updated_at) VALUES
(3, 4, 3,
    JSON_OBJECT('text', 'The orientation week ends with a ______ ceremony on Friday evening.'),
    JSON_OBJECT('value', 'welcome'),
    NOW(), NOW()),
(3, 5, 3,
    JSON_OBJECT('text', 'Students must register for modules by ______ of the first week.'),
    JSON_OBJECT('value', 'Friday'),
    NOW(), NOW());

-- ============================================================
-- LESSON 4: WRITING — Society
-- ============================================================
INSERT INTO lessons (id, title, content, category_id, lesson_type_id, created_at, updated_at)
VALUES (4, 'IELTS Writing Task 2 — Technology and Social Interaction', '', 5, 3, NOW(), NOW());

INSERT INTO sections (lesson_id, position, section_type_id, question, created_at, updated_at) VALUES
(4, 1, 6,
    JSON_OBJECT(
        'instruction',  'You should spend about 40 minutes on this task.',
        'prompt',       'Some people believe that modern technology has made people more isolated and less able to communicate face-to-face. Others argue that technology has improved communication and brought people closer together. Discuss both views and give your own opinion.',
        'requirements', 'Give reasons for your answer and include any relevant examples from your own knowledge or experience. Write at least 250 words.'
    ),
    NOW(), NOW());

-- ============================================================
-- LESSON 5: SPEAKING — Health
-- ============================================================
INSERT INTO lessons (id, title, content, category_id, lesson_type_id, created_at, updated_at)
VALUES (5, 'IELTS Speaking — A Healthy Lifestyle', '', 4, 4, NOW(), NOW());

INSERT INTO sections (lesson_id, position, section_type_id, question, created_at, updated_at) VALUES
(5, 1, 7,
    JSON_OBJECT(
        'part',        'Part 1 - Warm-up Questions',
        'instruction', 'Answer each question in 2-3 sentences.',
        'questions',   JSON_ARRAY(
            'Do you think you have a healthy lifestyle? Why or why not?',
            'What do you usually eat for breakfast?',
            'How often do you exercise?'
        )
    ),
    NOW(), NOW()),
(5, 2, 7,
    JSON_OBJECT(
        'part',        'Part 2 - Individual Long Turn',
        'instruction', 'You have 1 minute to prepare. Then speak for 1-2 minutes.',
        'cueCard',     JSON_OBJECT(
            'prompt', 'Describe a healthy habit you have developed.',
            'points', JSON_ARRAY(
                'What the habit is',
                'When and how you started it',
                'How it has affected your life',
                'Why you would recommend it to others'
            )
        )
    ),
    NOW(), NOW()),
(5, 3, 7,
    JSON_OBJECT(
        'part',        'Part 3 - Two-way Discussion',
        'instruction', 'Discuss the following questions. Give extended answers.',
        'questions',   JSON_ARRAY(
            'Why do many people struggle to maintain a healthy lifestyle in modern society?',
            'Should governments do more to promote public health? How?',
            'How has awareness of healthy living changed compared to previous generations?'
        )
    ),
    NOW(), NOW());

-- ============================================================
-- USERS (password: 123)
-- ============================================================
INSERT INTO users (firstname, lastname, username, email, password_hash, role, created_at, updated_at) VALUES
('Admin', 'System', 'admin', 'admin@learningenglish.com', '$2b$10$MKhEXCfIbQIc0HqGG6OH3uRJYASHs1fXgJzNTDrmj7VwBRjsWnKpy', 'ADMIN', NOW(), NOW()),
('Nguyen', 'Van A',  'user1', 'user1@gmail.com',           '$2b$10$MKhEXCfIbQIc0HqGG6OH3uRJYASHs1fXgJzNTDrmj7VwBRjsWnKpy', 'USER',  NOW(), NOW()),
('Tran',   'Thi B',  'user2', 'user2@gmail.com',           '$2b$10$MKhEXCfIbQIc0HqGG6OH3uRJYASHs1fXgJzNTDrmj7VwBRjsWnKpy', 'USER',  NOW(), NOW());

-- ============================================================
-- VIP PACKAGES
-- ============================================================
INSERT INTO vip_packages (name, description, months, price, is_active) VALUES
('VIP 1 month',  'Full access for 1 month.',  1,  99000.00, 1),
('VIP 3 months', 'Full access for 3 months.', 3, 249000.00, 1),
('VIP 12 months','Full access for 12 months.',12, 799000.00, 1);