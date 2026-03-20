package lk.watupa.vote.service;

import lk.watupa.vote.enums.VoteType;
import lk.watupa.vote.model.Vote;
import lk.watupa.vote.payload.VoteResponse;
import lk.watupa.vote.payload.VoteSummaryResponse;
import lk.watupa.vote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteService {

    private final VoteRepository voteRepository;

    @Value("${vote.approval.threshold:5}")
    private int approvalThreshold;

    @Transactional
    public VoteResponse castVote(Long submissionId, Long userId, VoteType voteType) {
        log.info("User {} casting {} on submission {}", userId, voteType, submissionId);

        int updatedRows = voteRepository.updateVoteType(submissionId, userId, voteType);
        if (updatedRows == 0) {
            try {
                voteRepository.saveAndFlush(new Vote(submissionId, userId, voteType));
            } catch (DataIntegrityViolationException e) {
                log.warn("Concurrent vote insert detected for submissionId={}, userId={}. Retrying as update.",
                        submissionId, userId, e);
                int retryUpdatedRows = voteRepository.updateVoteType(submissionId, userId, voteType);
                if (retryUpdatedRows == 0) {
                    throw new RuntimeException("Unable to persist vote due to a concurrent update", e);
                }
            }
        }

        Vote vote = voteRepository.findBySubmissionIdAndUserId(submissionId, userId)
                .orElseThrow(() -> new RuntimeException("Unable to load saved vote"));
        return mapToVoteResponse(vote);
    }

    public List<VoteResponse> getVotesForSubmission(Long submissionId) {
        log.info("Fetching votes for submission {}", submissionId);
        List<Vote> votes = voteRepository.findBySubmissionId(submissionId);
        return votes.stream()
                .map(this::mapToVoteResponse)
                .collect(Collectors.toList());
    }

    public VoteSummaryResponse calculateVoteScore(Long submissionId) {
        log.info("Calculating vote score for submission {}", submissionId);
        long upvotes = voteRepository.countBySubmissionIdAndVoteType(submissionId, VoteType.UPVOTE);
        long downvotes = voteRepository.countBySubmissionIdAndVoteType(submissionId, VoteType.DOWNVOTE);
        long netScore = upvotes - downvotes;
        boolean approved = netScore >= approvalThreshold;

        log.info("Submission {} - upvotes: {}, downvotes: {}, netScore: {}, approved: {}",
                submissionId, upvotes, downvotes, netScore, approved);

        return new VoteSummaryResponse(submissionId, upvotes, downvotes, netScore, approved);
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

