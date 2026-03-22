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
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    Page<Submission> findByStatus(Status status, Pageable pageable);

    Page<Submission> findByCompanyNameIgnoreCaseAndStatus(String companyName, Status status, Pageable pageable);

    Page<Submission> findByJobTitleIgnoreCaseAndStatus(String jobTitle, Status status, Pageable pageable);

    Page<Submission> findByExperienceLevelAndStatus(ExperienceLevel level, Status status, Pageable pageable);

    @Query("""
        SELECT s FROM Submission s
        WHERE s.status = 'APPROVED'
          AND (:companyName IS NULL OR LOWER(s.companyName) LIKE LOWER(CONCAT('%', :companyName, '%')))
          AND (:jobTitle    IS NULL OR LOWER(s.jobTitle)    LIKE LOWER(CONCAT('%', :jobTitle,    '%')))
          AND (:level       IS NULL OR s.experienceLevel = :level)
        """)
    Page<Submission> search(
            @Param("companyName") String companyName,
            @Param("jobTitle")    String jobTitle,
            @Param("level")       ExperienceLevel level,
            Pageable pageable);

    long countByStatus(Status status);
}
