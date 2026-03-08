package lk.watupa.vote.payload;

import jakarta.validation.constraints.NotNull;
import lk.watupa.vote.enums.VoteType;
import lombok.Data;

@Data
public class VoteRequest {

    @NotNull(message = "Submission ID is required")
    private Long submissionId;

    @NotNull(message = "Vote type is required")
    private VoteType voteType;
}

