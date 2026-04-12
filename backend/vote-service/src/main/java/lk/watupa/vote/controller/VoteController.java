package lk.watupa.vote.controller;

import jakarta.validation.Valid;
import lk.watupa.vote.payload.VoteCountDto;
import lk.watupa.vote.payload.VoteRequest;
import lk.watupa.vote.payload.VoteResponse;
import lk.watupa.vote.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<VoteResponse> castVote(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody VoteRequest voteRequest) {
        VoteResponse voteResponse = voteService.castVote(
                voteRequest.getSubmissionId(),
                userId,
                voteRequest.getVoteType());

        return new ResponseEntity<>(voteResponse, HttpStatus.OK);
    }

    @PostMapping("/counts")
    public ResponseEntity<List<VoteCountDto>> getVoteCounts(
            @RequestBody List<String> submissionIds) {
        return ResponseEntity.ok(voteService.getVoteCountsBySubmissionIds(submissionIds));
    }
}


