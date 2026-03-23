package lk.watupa.vote.repository;

import lk.watupa.vote.model.VoteCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteCountRepository extends JpaRepository<VoteCount, Long> {

    @Modifying
    @Query(value = """
            INSERT INTO community.vote_counts (submission_id, upvote_count, downvote_count, updated_at)
            SELECT :submissionId, 0, 0, CURRENT_TIMESTAMP
            WHERE NOT EXISTS (
                SELECT 1 FROM community.vote_counts WHERE submission_id = :submissionId
            )
            """, nativeQuery = true)
    void ensureSubmissionCountExists(@Param("submissionId") Long submissionId);

    @Modifying
    @Query(value = """
            UPDATE community.vote_counts
            SET upvote_count = upvote_count + :upvoteDelta,
                downvote_count = downvote_count + :downvoteDelta,
                updated_at = CURRENT_TIMESTAMP
            WHERE submission_id = :submissionId
            """, nativeQuery = true)
    int applyVoteDelta(@Param("submissionId") Long submissionId,
                       @Param("upvoteDelta") int upvoteDelta,
                       @Param("downvoteDelta") int downvoteDelta);
}


