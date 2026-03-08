package lk.watupa.vote.controller;

import jakarta.validation.Valid;
import lk.watupa.vote.payload.VoteRequest;
import lk.watupa.vote.payload.VoteResponse;
import lk.watupa.vote.payload.VoteSummaryResponse;
import lk.watupa.vote.service.VoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
@Slf4j
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<VoteResponse> castVote(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody VoteRequest voteRequest) {

        log.info("Received vote request from userId={} for submissionId={}",
                userId, voteRequest.getSubmissionId());

        VoteResponse voteResponse = voteService.castVote(
                voteRequest.getSubmissionId(),
                userId,
                voteRequest.getVoteType());

        return new ResponseEntity<>(voteResponse, HttpStatus.OK);
    }

    @GetMapping("/{submissionId}")
    public ResponseEntity<List<VoteResponse>> getVotesForSubmission(
            @PathVariable Long submissionId) {

        log.info("Fetching votes for submissionId={}", submissionId);
        List<VoteResponse> votes = voteService.getVotesForSubmission(submissionId);
        return new ResponseEntity<>(votes, HttpStatus.OK);
    }

    @GetMapping("/{submissionId}/summary")
    public ResponseEntity<VoteSummaryResponse> getVoteSummary(
            @PathVariable Long submissionId) {

        log.info("Fetching vote summary for submissionId={}", submissionId);
        VoteSummaryResponse summary = voteService.calculateVoteScore(submissionId);
        return new ResponseEntity<>(summary, HttpStatus.OK);
    }
}


