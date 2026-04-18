-- identity schema

CREATE SCHEMA IF NOT EXISTS identity;

CREATE TABLE IF NOT EXISTS identity.users (
    user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(20) NOT NULL,
    email VARCHAR(50) NOT NULL,
    password VARCHAR(120) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email)
);

-- salary schema

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

-- community schema

CREATE SCHEMA IF NOT EXISTS community;

CREATE TABLE IF NOT EXISTS community.votes (
    id            BIGINT      GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    submission_id BIGINT      NOT NULL,
    user_id       BIGINT      NOT NULL,
    vote_type     VARCHAR(10) NOT NULL CHECK (vote_type IN ('UPVOTE', 'DOWNVOTE')),
    created_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_votes_submission_user UNIQUE (submission_id, user_id)
);

CREATE TABLE IF NOT EXISTS community.vote_counts (
    submission_id  BIGINT    PRIMARY KEY,
    upvote_count   BIGINT    NOT NULL DEFAULT 0,
    downvote_count BIGINT    NOT NULL DEFAULT 0,
    updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);