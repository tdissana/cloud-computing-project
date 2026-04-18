package lk.watupa.vote.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteSummaryResponse {

    private Long submissionId;
    private long upvotes;
    private long downvotes;
    private long netScore;
    private boolean approved;
}

