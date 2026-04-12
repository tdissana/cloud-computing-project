package lk.watupa.vote.model;

import jakarta.persistence.*;
import lk.watupa.vote.enums.VoteType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(
        schema = "community",
        name = "votes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"submission_id", "user_id"})
        })
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "submission_id", nullable = false)
    private Long submissionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "vote_type", nullable = false, length = 10)
    private VoteType voteType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Vote(Long submissionId, Long userId, VoteType voteType) {
        this.submissionId = submissionId;
        this.userId = userId;
        this.voteType = voteType;
    }
}

