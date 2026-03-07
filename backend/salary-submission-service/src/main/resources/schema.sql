CREATE SCHEMA IF NOT EXISTS salary;

DROP TABLE IF EXISTS salary.submissions;

CREATE TABLE salary.submissions
(
    id                 VARCHAR(36) PRIMARY KEY,
    company            VARCHAR(255),
    role               VARCHAR(255),
    experience_level   VARCHAR(50),
    country            VARCHAR(100) DEFAULT 'Sri Lanka',
    total_compensation DOUBLE,
    base_salary        DOUBLE,
    currency           VARCHAR(10)  DEFAULT 'LKR',
    anonymize          BOOLEAN      DEFAULT TRUE,
    status             VARCHAR(20)  DEFAULT 'PENDING',
    created_at         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);