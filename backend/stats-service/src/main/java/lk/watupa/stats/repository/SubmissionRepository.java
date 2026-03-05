package lk.watupa.stats.repository;

import lk.watupa.stats.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @Query("""
        SELECT s FROM Submission s
        WHERE s.status = 'APPROVED'
          AND (:role    IS NULL OR LOWER(s.role)    = LOWER(:role))
          AND (:company IS NULL OR LOWER(s.company) = LOWER(:company))
          AND (:level   IS NULL OR LOWER(s.level)   = LOWER(:level))
          AND (:country IS NULL OR LOWER(s.country) = LOWER(:country))
    """)
    List<Submission> findApprovedByFilters(
            @Param("role")    String role,
            @Param("company") String company,
            @Param("level")   String level,
            @Param("country") String country
    );
}