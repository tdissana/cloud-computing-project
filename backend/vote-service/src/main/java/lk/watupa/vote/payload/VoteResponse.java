package lk.watupa.vote.payload;

import lk.watupa.vote.enums.VoteType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteResponse {

    private Long id;
    private UUID submissionId;
    private Long userId;
    private VoteType voteType;
    private LocalDateTime createdAt;
}

