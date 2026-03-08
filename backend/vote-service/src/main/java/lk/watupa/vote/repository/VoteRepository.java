package lk.watupa.vote.repository;

import lk.watupa.vote.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    List<Vote> findBySubmissionId(Long submissionId);

    Optional<Vote> findBySubmissionIdAndUserId(Long submissionId, Long userId);
}

