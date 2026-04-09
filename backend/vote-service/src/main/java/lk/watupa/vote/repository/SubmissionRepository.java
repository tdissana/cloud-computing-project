package lk.watupa.vote.repository;

import lk.watupa.vote.enums.Status;
import lk.watupa.vote.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, UUID> {

    @Modifying
    @Query("""
            update Submission s
            set s.status = :approvedStatus
            where s.id = :submissionId and s.status <> :approvedStatus
            """)
    int updateStatusToApproved(@Param("submissionId") UUID submissionId,
                               @Param("approvedStatus") Status approvedStatus);
}


