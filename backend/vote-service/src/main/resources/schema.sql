CREATE SCHEMA IF NOT EXISTS community;

CREATE TABLE IF NOT EXISTS community.votes (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    submission_id BIGINT      NOT NULL,
    user_id       BIGINT      NOT NULL,
    vote_type     VARCHAR(10) NOT NULL CHECK (vote_type IN ('UPVOTE', 'DOWNVOTE')),
    created_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_votes_submission_user UNIQUE (submission_id, user_id)
);

