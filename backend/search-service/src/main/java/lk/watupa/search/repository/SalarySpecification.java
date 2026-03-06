package lk.watupa.search.repository;

import jakarta.persistence.criteria.Predicate;
import lk.watupa.search.payload.SalarySearchRequest;
import lk.watupa.search.model.ApprovedSalary;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds a JPA Specification dynamically from the search request.
 * Each filter is only applied when its corresponding field is non-null/non-blank.
 */
public class SalarySpecification {

    private SalarySpecification() {}

    public static Specification<ApprovedSalary> fromRequest(SalarySearchRequest req) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (hasValue(req.getCountry())) {
                predicates.add(cb.like(
                        cb.lower(root.get("country")),
                        "%" + req.getCountry().toLowerCase() + "%"));
            }

            if (hasValue(req.getCompany())) {
                // Only match non-anonymized records when filtering by company
                predicates.add(cb.and(
                        cb.equal(root.get("anonymized"), false),
                        cb.like(cb.lower(root.get("companyName")),
                                "%" + req.getCompany().toLowerCase() + "%")
                ));
            }

            if (hasValue(req.getJobTitle())) {
                predicates.add(cb.like(
                        cb.lower(root.get("jobTitle")),
                        "%" + req.getJobTitle().toLowerCase() + "%"));
            }

            if (hasValue(req.getSeniorityLevel())) {
                predicates.add(cb.equal(
                        cb.lower(root.get("seniorityLevel")),
                        req.getSeniorityLevel().toLowerCase()));
            }

            if (hasValue(req.getEmploymentType())) {
                predicates.add(cb.equal(
                        cb.lower(root.get("employmentType")),
                        req.getEmploymentType().toLowerCase()));
            }

            if (hasValue(req.getCurrency())) {
                predicates.add(cb.equal(
                        cb.upper(root.get("currency")),
                        req.getCurrency().toUpperCase()));
            }

            if (req.getMinExperience() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("yearsOfExperience"), req.getMinExperience()));
            }

            if (req.getMaxExperience() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("yearsOfExperience"), req.getMaxExperience()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static boolean hasValue(String s) {
        return s != null && !s.isBlank();
    }
}