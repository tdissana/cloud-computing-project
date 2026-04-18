package lk.watupa.vote.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(schema = "community", name = "vote_counts")
public class VoteCount {

    @Id
    @Column(name = "submission_id", nullable = false)
    private Long submissionId;

    @Column(name = "upvote_count", nullable = false)
    private long upvoteCount;

    @Column(name = "downvote_count", nullable = false)
    private long downvoteCount;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

