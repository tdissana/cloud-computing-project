package lk.watupa.vote.service;

import lk.watupa.vote.enums.VoteType;
import lk.watupa.vote.model.Vote;
import lk.watupa.vote.payload.VoteResponse;
import lk.watupa.vote.repository.VoteCountRepository;
import lk.watupa.vote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteService {

    private static final String PERSISTENCE_ERROR = "Unable to persist vote due to a concurrent update";

    private final VoteRepository voteRepository;
    private final VoteCountRepository voteCountRepository;

    @Transactional
    public VoteResponse castVote(Long submissionId, Long userId, VoteType voteType) {
        log.info("User {} casting {} on submission {}", userId, voteType, submissionId);

        Vote existingVote = voteRepository.findBySubmissionIdAndUserIdForUpdate(submissionId, userId)
                .orElse(null);

        int upvoteDelta = 0;
        int downvoteDelta = 0;

        if (existingVote == null) {
            try {
                voteRepository.saveAndFlush(new Vote(submissionId, userId, voteType));
                if (voteType == VoteType.UPVOTE) {
                    upvoteDelta = 1;
                } else {
                    downvoteDelta = 1;
                }
            } catch (DataIntegrityViolationException e) {
                log.warn("Concurrent vote insert detected for submissionId={}, userId={}. Retrying as update.",
                        submissionId, userId, e);

                Vote concurrentVote = voteRepository.findBySubmissionIdAndUserIdForUpdate(submissionId, userId)
                        .orElseThrow(() -> new RuntimeException(PERSISTENCE_ERROR, e));

                if (concurrentVote.getVoteType() != voteType) {
                    int updatedRows = voteRepository.updateVoteType(submissionId, userId, voteType);
                    if (updatedRows == 0) {
                        throw new RuntimeException(PERSISTENCE_ERROR, e);
                    }
                    upvoteDelta = voteType == VoteType.UPVOTE ? 1 : -1;
                    downvoteDelta = voteType == VoteType.DOWNVOTE ? 1 : -1;
                }
            }
        } else if (existingVote.getVoteType() != voteType) {
            VoteType previousVoteType = existingVote.getVoteType();
            int updatedRows = voteRepository.updateVoteType(submissionId, userId, voteType);
            if (updatedRows == 0) {
                throw new RuntimeException(PERSISTENCE_ERROR);
            }

            if (previousVoteType == VoteType.UPVOTE) {
                upvoteDelta -= 1;
            } else {
                downvoteDelta -= 1;
            }

            if (voteType == VoteType.UPVOTE) {
                upvoteDelta += 1;
            } else {
                downvoteDelta += 1;
            }
        }

        applyCountDelta(submissionId, upvoteDelta, downvoteDelta);

        Vote vote = voteRepository.findBySubmissionIdAndUserIdForUpdate(submissionId, userId)
                .orElseThrow(() -> new RuntimeException("Unable to load saved vote"));
        return mapToVoteResponse(vote);
    }

    private void applyCountDelta(Long submissionId, int upvoteDelta, int downvoteDelta) {
        if (upvoteDelta == 0 && downvoteDelta == 0) {
            return;
        }
        voteCountRepository.ensureSubmissionCountExists(submissionId);
        int updatedRows = voteCountRepository.applyVoteDelta(submissionId, upvoteDelta, downvoteDelta);
        if (updatedRows == 0) {
            throw new RuntimeException("Unable to update vote count reference for submission " + submissionId);
        }
    }

    private VoteResponse mapToVoteResponse(Vote vote) {
        VoteResponse response = new VoteResponse();
        response.setId(vote.getId());
        response.setSubmissionId(vote.getSubmissionId());
        response.setUserId(vote.getUserId());
        response.setVoteType(vote.getVoteType());
        response.setCreatedAt(vote.getCreatedAt());
        return response;
    }
}

