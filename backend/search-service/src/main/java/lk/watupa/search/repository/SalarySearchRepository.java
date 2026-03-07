package lk.watupa.search.repository;

import lk.watupa.search.model.ApprovedSalary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SalarySearchRepository
        extends JpaRepository<ApprovedSalary, UUID>, JpaSpecificationExecutor<ApprovedSalary> {

    // --- Distinct values for filter dropdowns ---

    @Query("SELECT DISTINCT s.country FROM ApprovedSalary s WHERE s.country IS NOT NULL ORDER BY s.country")
    List<String> findDistinctCountries();

    @Query("SELECT DISTINCT s.companyName FROM ApprovedSalary s WHERE s.companyName IS NOT NULL AND s.anonymized = false ORDER BY s.companyName")
    List<String> findDistinctCompanies();

    @Query("SELECT DISTINCT s.jobTitle FROM ApprovedSalary s WHERE s.jobTitle IS NOT NULL ORDER BY s.jobTitle")
    List<String> findDistinctJobTitles();

    @Query("SELECT DISTINCT s.seniorityLevel FROM ApprovedSalary s WHERE s.seniorityLevel IS NOT NULL ORDER BY s.seniorityLevel")
    List<String> findDistinctSeniorityLevels();

    @Query("SELECT DISTINCT s.employmentType FROM ApprovedSalary s WHERE s.employmentType IS NOT NULL ORDER BY s.employmentType")
    List<String> findDistinctEmploymentTypes();

    @Query("SELECT DISTINCT s.currency FROM ApprovedSalary s WHERE s.currency IS NOT NULL ORDER BY s.currency")
    List<String> findDistinctCurrencies();
}