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