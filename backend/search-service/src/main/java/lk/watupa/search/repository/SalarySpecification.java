package lk.watupa.search.repository;

import jakarta.persistence.criteria.Predicate;
import lk.watupa.search.model.ApprovedSalary;
import lk.watupa.search.payload.SalarySearchRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds a JPA Specification dynamically from the search request.
 * Restricts by verification status (verified = APPROVED only; unverified = not approved).
 */
public class SalarySpecification {

    private SalarySpecification() {}

    public static Specification<ApprovedSalary> fromRequest(SalarySearchRequest req) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            String vs = req.getVerificationStatus();
            if (vs != null && !vs.isBlank()) {
                if ("VERIFIED".equalsIgnoreCase(vs)) {
                    predicates.add(cb.equal(root.get("status"), "APPROVED"));
                } else if ("UNVERIFIED".equalsIgnoreCase(vs)) {
                    predicates.add(cb.notEqual(root.get("status"), "APPROVED"));
                }
                // If "BOTH", no status filter is applied (shows all statuses)
            } else {
                // Default to VERIFIED if not specified
                predicates.add(cb.equal(root.get("status"), "APPROVED"));
            }

            if (hasValue(req.getCountry())) {
                predicates.add(cb.like(
                        cb.lower(root.get("country")),
                        "%" + req.getCountry().toLowerCase() + "%"));
            }

            if (hasValue(req.getCompany())) {
                predicates.add(cb.and(
                        cb.equal(root.get("anonymize"), false),
                        cb.like(cb.lower(root.get("companyName")),
                                "%" + req.getCompany().toLowerCase() + "%")
                ));
            }

            if (hasValue(req.getJobTitle())) {
                predicates.add(cb.like(
                        cb.lower(root.get("jobTitle")),
                        "%" + req.getJobTitle().toLowerCase() + "%"));
            }

            if (hasValue(req.getExperienceLevel())) {
                predicates.add(cb.equal(
                        cb.lower(root.get("experienceLevel")),
                        req.getExperienceLevel().toLowerCase()));
            }

            if (hasValue(req.getCurrency())) {
                predicates.add(cb.equal(
                        cb.upper(root.get("currency")),
                        req.getCurrency().toUpperCase()));
            }

            if (req.getMinSeniority() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("seniority"), req.getMinSeniority()));
            }

            if (req.getMaxSeniority() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("seniority"), req.getMaxSeniority()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static boolean hasValue(String s) {
        return s != null && !s.isBlank();
    }
}
