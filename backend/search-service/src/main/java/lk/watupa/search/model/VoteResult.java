package lk.watupa.search.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Vote aggregates keyed by submission id (same logical id as salary.submission.id, stored as varchar).
 */
@Entity
@Table(name = "voteresults", schema = "vote")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteResult {

    @Id
    @Column(name = "id", length = 255)
    private String id;

    @Column(name = "up_vote_count", nullable = false)
    private int upVoteCount;

    @Column(name = "down_vote_count", nullable = false)
    private int downVoteCount;
}
