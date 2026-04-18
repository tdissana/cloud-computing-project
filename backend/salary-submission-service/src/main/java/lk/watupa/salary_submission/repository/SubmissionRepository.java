package lk.watupa.salary_submission.repository;

import lk.watupa.salary_submission.enums.ExperienceLevel;
import lk.watupa.salary_submission.enums.Status;
import lk.watupa.salary_submission.model.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long>,
        JpaSpecificationExecutor<Submission> {

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

    @Query("SELECT DISTINCT s.country FROM Submission s WHERE s.country IS NOT NULL ORDER BY s.country")
    List<String> findDistinctCountries();

    @Query("SELECT DISTINCT s.companyName FROM Submission s WHERE s.companyName IS NOT NULL AND s.anonymize = false ORDER BY s.companyName")
    List<String> findDistinctCompanies();

    @Query("SELECT DISTINCT s.jobTitle FROM Submission s WHERE s.jobTitle IS NOT NULL ORDER BY s.jobTitle")
    List<String> findDistinctJobTitles();

    @Query("SELECT DISTINCT s.experienceLevel FROM Submission s WHERE s.experienceLevel IS NOT NULL ORDER BY s.experienceLevel")
    List<String> findDistinctExperienceLevels();

    @Query("SELECT DISTINCT s.currency FROM Submission s WHERE s.currency IS NOT NULL ORDER BY s.currency")
    List<String> findDistinctCurrencies();
}
