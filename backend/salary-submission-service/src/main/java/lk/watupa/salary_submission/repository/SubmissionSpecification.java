package lk.watupa.salary_submission.repository;

import jakarta.persistence.criteria.Predicate;
import lk.watupa.salary_submission.enums.EmploymentType;
import lk.watupa.salary_submission.enums.ExperienceLevel;
import lk.watupa.salary_submission.enums.Status;
import lk.watupa.salary_submission.model.Submission;
import lk.watupa.salary_submission.payload.SubmissionSearchRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class SubmissionSpecification {

    private SubmissionSpecification() {}

    public static Specification<Submission> fromRequest(SubmissionSearchRequest req) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            String vs = req.verificationStatus();
            if (vs != null && !vs.isBlank()) {
                if ("VERIFIED".equalsIgnoreCase(vs)) {
                    predicates.add(cb.equal(root.get("status"), Status.APPROVED));
                } else if ("UNVERIFIED".equalsIgnoreCase(vs)) {
                    predicates.add(cb.notEqual(root.get("status"), Status.APPROVED));
                }
            } else {
                predicates.add(cb.equal(root.get("status"), Status.APPROVED));
            }

            if (hasValue(req.country())) {
                predicates.add(cb.like(
                        cb.lower(root.get("country")),
                        "%" + req.country().toLowerCase() + "%"));
            }

            if (hasValue(req.company())) {
                predicates.add(cb.and(
                        cb.equal(root.get("anonymize"), false),
                        cb.like(cb.lower(root.get("companyName")),
                                "%" + req.company().toLowerCase() + "%")
                ));
            }

            if (hasValue(req.jobTitle())) {
                predicates.add(cb.like(
                        cb.lower(root.get("jobTitle")),
                        "%" + req.jobTitle().toLowerCase() + "%"));
            }

            if (hasValue(req.experienceLevel())) {
                try {
                    ExperienceLevel level = ExperienceLevel.valueOf(
                            titleCase(req.experienceLevel()));
                    predicates.add(cb.equal(root.get("experienceLevel"), level));
                } catch (IllegalArgumentException ignored) {
                    predicates.add(cb.disjunction());
                }
            }

            if (hasValue(req.employmentType())) {
                try {
                    EmploymentType type = EmploymentType.from(req.employmentType());
                    predicates.add(cb.equal(root.get("employmentType"), type));
                } catch (IllegalArgumentException ignored) {
                    predicates.add(cb.disjunction());
                }
            }

            if (hasValue(req.currency())) {
                predicates.add(cb.equal(
                        cb.upper(root.get("currency")),
                        req.currency().toUpperCase()));
            }

            if (req.minSeniority() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("seniority"), req.minSeniority()));
            }

            if (req.maxSeniority() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("seniority"), req.maxSeniority()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static boolean hasValue(String s) {
        return s != null && !s.isBlank();
    }

    private static String titleCase(String s) {
        if (s == null || s.isBlank()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }
}
