package lk.watupa.vote.repository;

import lk.watupa.vote.enums.VoteType;
import lk.watupa.vote.model.Vote;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from Vote v where v.submissionId = :submissionId and v.userId = :userId")
    Optional<Vote> findBySubmissionIdAndUserIdForUpdate(@Param("submissionId") Long submissionId,
                                                         @Param("userId") Long userId);

    @Modifying
    @Query("update Vote v set v.voteType = :voteType where v.submissionId = :submissionId and v.userId = :userId")
    int updateVoteType(@Param("submissionId") Long submissionId,
                       @Param("userId") Long userId,
                       @Param("voteType") VoteType voteType);
}

