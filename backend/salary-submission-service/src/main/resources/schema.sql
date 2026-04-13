CREATE SCHEMA IF NOT EXISTS salary;

CREATE TABLE IF NOT EXISTS salary.submission
(
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    company_name       VARCHAR(255),
    job_title          VARCHAR(255),
    experience_level   VARCHAR(50),
    employment_type    VARCHAR(50),
    seniority          INT,
    country            VARCHAR(100) DEFAULT 'Sri Lanka',
    currency           VARCHAR(10)  DEFAULT 'LKR',
    total_compensation DOUBLE PRECISION,
    base_salary        DOUBLE PRECISION,
    skills             TEXT,
    anonymize          BOOLEAN      DEFAULT TRUE,
    status             VARCHAR(20)  DEFAULT 'PENDING',
    timestamp          TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);