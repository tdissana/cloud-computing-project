package lk.watupa.vote.payload;

import lk.watupa.vote.enums.VoteType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteResponse {

    private Long id;
    private Long submissionId;
    private Long userId;
    private VoteType voteType;
    private LocalDateTime createdAt;
    private int upvoteCount;
    private int downvoteCount;
}

