-- Must match salary-submission-service/src/main/resources/schema.sql (same table contract).
-- Search-service uses its own H2 database (jdbc:h2:mem:searchdb), not the submission service process/DB.
CREATE SCHEMA IF NOT EXISTS salary;

DROP TABLE IF EXISTS salary.submission;

CREATE TABLE salary.submission
(
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    company_name       VARCHAR(255),
    job_title          VARCHAR(255),
    experience_level   VARCHAR(50),
    seniority          INT,
    country            VARCHAR(100) DEFAULT 'Sri Lanka',
    currency           VARCHAR(10)  DEFAULT 'LKR',
    total_compensation DOUBLE,
    base_salary        DOUBLE,
    skills             TEXT,
    anonymize          BOOLEAN      DEFAULT TRUE,
    status             VARCHAR(20)  DEFAULT 'PENDING',
    timestamp          TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- Vote aggregates (separate schema). id matches salary.submission.id as string.
CREATE SCHEMA IF NOT EXISTS vote;

DROP TABLE IF EXISTS vote.voteresults;

CREATE TABLE vote.voteresults
(
    id               VARCHAR(255) PRIMARY KEY,
    up_vote_count    INT NOT NULL DEFAULT 0,
    down_vote_count  INT NOT NULL DEFAULT 0
);
