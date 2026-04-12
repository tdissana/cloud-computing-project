package lk.watupa.vote.service;

import lk.watupa.vote.client.SalarySubmissionClient;
import lk.watupa.vote.enums.VoteType;
import lk.watupa.vote.model.Vote;
import lk.watupa.vote.model.VoteCount;
import lk.watupa.vote.payload.VoteCountDto;
import lk.watupa.vote.payload.VoteResponse;
import lk.watupa.vote.repository.VoteCountRepository;
import lk.watupa.vote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteService {

    private static final String PERSISTENCE_ERROR = "Unable to persist vote due to a concurrent update";

    private final VoteRepository voteRepository;
    private final VoteCountRepository voteCountRepository;
    private final SalarySubmissionClient salarySubmissionClient;

    @Value("${vote.approval.threshold:10}")
    private int voteApprovalThreshold;

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
        updateSubmissionStatusIfThresholdReached(submissionId);

        Vote vote = voteRepository.findBySubmissionIdAndUserIdForUpdate(submissionId, userId)
                .orElseThrow(() -> new RuntimeException("Unable to load saved vote"));
        VoteCount voteCount = voteCountRepository.findById(submissionId).orElse(null);
        return mapToVoteResponse(vote, voteCount);
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

    private void updateSubmissionStatusIfThresholdReached(Long submissionId) {
        VoteCount voteCount = voteCountRepository.findById(submissionId).orElse(null);
        if (voteCount == null) {
            return;
        }

        long totalVotes = voteCount.getUpvoteCount() + voteCount.getDownvoteCount();
        if (totalVotes < voteApprovalThreshold) {
            return;
        }

        try {
            salarySubmissionClient.updateStatus(submissionId.toString(), "APPROVED");
            log.info("Submission {} marked as APPROVED after reaching {} votes", submissionId, totalVotes);
        } catch (Exception e) {
            log.warn("Failed to update submission {} status via salary-submission-service: {}",
                    submissionId, e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<VoteCountDto> getVoteCountsBySubmissionIds(List<String> submissionIds) {
        List<Long> ids = new ArrayList<>();
        for (String id : submissionIds) {
            try {
                ids.add(Long.parseLong(id));
            } catch (NumberFormatException ignored) {
                // skip non-numeric ids
            }
        }
        if (ids.isEmpty()) {
            return List.of();
        }
        return voteCountRepository.findBySubmissionIdIn(ids).stream()
                .map(vc -> new VoteCountDto(
                        vc.getSubmissionId().toString(),
                        (int) vc.getUpvoteCount(),
                        (int) vc.getDownvoteCount()))
                .toList();
    }

    private VoteResponse mapToVoteResponse(Vote vote, VoteCount voteCount) {
        VoteResponse response = new VoteResponse();
        response.setId(vote.getId());
        response.setSubmissionId(vote.getSubmissionId());
        response.setUserId(vote.getUserId());
        response.setVoteType(vote.getVoteType());
        response.setCreatedAt(vote.getCreatedAt());
        if (voteCount != null) {
            response.setUpvoteCount((int) voteCount.getUpvoteCount());
            response.setDownvoteCount((int) voteCount.getDownvoteCount());
        }
        return response;
    }
}

