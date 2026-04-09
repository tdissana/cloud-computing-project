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
          AND (:jobTitle    IS NULL OR LOWER(s.jobTitle)         = LOWER(:jobTitle))
          AND (:companyName IS NULL OR LOWER(s.companyName)      = LOWER(:companyName))
          AND (:level       IS NULL OR LOWER(s.experienceLevel)  = LOWER(:level))
          AND (:country     IS NULL OR LOWER(s.country)          = LOWER(:country))
    """)
    List<Submission> findApprovedByFilters(
            @Param("jobTitle")     String jobTitle,
            @Param("companyName")  String companyName,
            @Param("level")        String level,
            @Param("country")      String country
    );
}