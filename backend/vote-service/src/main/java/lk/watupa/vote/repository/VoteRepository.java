package lk.watupa.vote.repository;

import lk.watupa.vote.enums.VoteType;
import lk.watupa.vote.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    List<Vote> findBySubmissionId(Long submissionId);

    Optional<Vote> findBySubmissionIdAndUserId(Long submissionId, Long userId);

    long countBySubmissionIdAndVoteType(Long submissionId, VoteType voteType);

    @Modifying
    @Query("update Vote v set v.voteType = :voteType where v.submissionId = :submissionId and v.userId = :userId")
    int updateVoteType(@Param("submissionId") Long submissionId,
                       @Param("userId") Long userId,
                       @Param("voteType") VoteType voteType);
}

