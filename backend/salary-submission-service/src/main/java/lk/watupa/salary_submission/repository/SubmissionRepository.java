package lk.watupa.salary_submission.repository;

import lk.watupa.salary_submission.enums.ExperienceLevel;
import lk.watupa.salary_submission.enums.Status;
import lk.watupa.salary_submission.model.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission,Long > {
    /** All approved submissions (public feed). */
    Page<Submission> findByStatus(Status status, Pageable pageable);

    /** Filter by company (case-insensitive) and status. */
    Page<Submission> findByCompanyIgnoreCaseAndStatus(String company, Status status, Pageable pageable);

    /** Filter by role and status. */
    Page<Submission> findByRoleIgnoreCaseAndStatus(String role, Status status, Pageable pageable);

    /** Filter by experience level and status. */
    Page<Submission> findByExperienceLevelAndStatus(ExperienceLevel level, Status status, Pageable pageable);

    /** Flexible multi-field search, approved only. */
    @Query("""
        SELECT s FROM Submission s
        WHERE s.status = 'APPROVED'
          AND (:company IS NULL OR LOWER(s.company) LIKE LOWER(CONCAT('%', :company, '%')))
          AND (:role    IS NULL OR LOWER(s.role)    LIKE LOWER(CONCAT('%', :role,    '%')))
          AND (:level   IS NULL OR s.experienceLevel = :level)
        """)
    Page<Submission> search(
            @Param("company") String company,
            @Param("role")    String role,
            @Param("level")   ExperienceLevel level,
            Pageable pageable);

    long countByStatus(Status status);
}
