package lk.watupa.vote.service;

import lk.watupa.vote.enums.VoteType;
import lk.watupa.vote.model.Vote;
import lk.watupa.vote.payload.VoteResponse;
import lk.watupa.vote.payload.VoteSummaryResponse;
import lk.watupa.vote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteService {

    private final VoteRepository voteRepository;

    @Value("${vote.approval.threshold:5}")
    private int approvalThreshold;

    public VoteResponse castVote(Long submissionId, Long userId, VoteType voteType) {
        log.info("User {} casting {} on submission {}", userId, voteType, submissionId);

        Optional<Vote> existing = voteRepository.findBySubmissionIdAndUserId(submissionId, userId);

        Vote vote;
        if (existing.isPresent()) {
            vote = existing.get();
            log.info("Updating existing vote id {} from {} to {}", vote.getId(), vote.getVoteType(), voteType);
            vote.setVoteType(voteType);
        } else {
            vote = new Vote(submissionId, userId, voteType);
        }

        try {
            vote = voteRepository.save(vote);
        } catch (Exception e) {
            log.error("Error saving vote: {}", e.getMessage());
            throw new RuntimeException("Error saving vote!");
        }

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
        List<Vote> votes = voteRepository.findBySubmissionId(submissionId);

        long upvotes = votes.stream().filter(v -> v.getVoteType() == VoteType.UPVOTE).count();
        long downvotes = votes.stream().filter(v -> v.getVoteType() == VoteType.DOWNVOTE).count();
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

