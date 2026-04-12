package lk.watupa.search.service;

import lk.watupa.search.client.SalarySubmissionClient;
import lk.watupa.search.client.VoteClient;
import lk.watupa.search.payload.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private static final List<String> EMPLOYMENT_TYPES = List.of(
            "Full-time", "Part-time", "Contract", "Freelance"
    );

    private static final DateTimeFormatter APPROVED_AT_FMT =
            DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);

    private final SalarySubmissionClient salarySubmissionClient;
    private final VoteClient voteClient;

    public PagedResponse<SalaryResultResponse> search(SalarySearchRequest request) {
        OwnerPagedDto<SubmissionDto> pagedResult = salarySubmissionClient.search(request);

        List<SubmissionDto> rows = pagedResult.content();
        List<String> idStrings = rows.stream()
                .map(s -> String.valueOf(s.id()))
                .toList();

        Map<String, VoteCountDto> votesById = voteClient.getVoteCounts(idStrings).stream()
                .collect(Collectors.toMap(VoteCountDto::submissionId, v -> v, (a, b) -> a));

        List<SalaryResultResponse> dtos = rows.stream()
                .map(s -> toDto(s, votesById.get(String.valueOf(s.id()))))
                .toList();

        List<SalaryResultResponse> sortedDtos = sortResults(dtos, request);

        log.info("Search returned {} results (page {}/{}) verificationStatus={}",
                sortedDtos.size(), pagedResult.page(), pagedResult.totalPages(),
                request.getVerificationStatus());

        return PagedResponse.<SalaryResultResponse>builder()
                .content(sortedDtos)
                .page(pagedResult.page())
                .size(pagedResult.size())
                .totalElements(pagedResult.totalElements())
                .totalPages(pagedResult.totalPages())
                .last(pagedResult.last())
                .build();
    }

    public FilterOptionsResponse getFilterOptions() {
        OwnerFilterOptionsDto opts = salarySubmissionClient.getFilterOptions();
        return FilterOptionsResponse.builder()
                .countries(opts.countries())
                .companies(opts.companies())
                .jobTitles(opts.jobTitles())
                .seniorityLevels(opts.experienceLevels())
                .employmentTypes(EMPLOYMENT_TYPES)
                .currencies(opts.currencies())
                .build();
    }

    private SalaryResultResponse toDto(SubmissionDto s, VoteCountDto vote) {
        boolean anon = Boolean.TRUE.equals(s.anonymize());
        int up = vote != null ? vote.upvoteCount() : 0;
        int down = vote != null ? vote.downvoteCount() : 0;

        Double gross = s.baseSalary();
        Double additional = null;
        if (s.totalCompensation() != null && s.baseSalary() != null) {
            double diff = s.totalCompensation() - s.baseSalary();
            if (diff > 0) {
                additional = diff;
            }
        }

        String approvedAt = s.timestamp() == null
                ? null
                : APPROVED_AT_FMT.format(s.timestamp().atOffset(ZoneOffset.UTC));

        String level = s.experienceLevel() == null
                ? ""
                : titleCaseWord(s.experienceLevel());

        return SalaryResultResponse.builder()
                .id(String.valueOf(s.id()))
                .companyName(anon ? "Anonymous" : s.companyName())
                .jobTitle(s.jobTitle())
                .seniorityLevel(level)
                .employmentType("Full-time")
                .country(s.country())
                .city(anon ? null : null)
                .grossMonthlySalary(gross)
                .currency(s.currency())
                .additionalCompensation(additional)
                .yearsOfExperience(s.seniority())
                .techStack(s.skills() != null ? s.skills() : "")
                .anonymized(anon)
                .approvedAt(approvedAt != null ? approvedAt : "")
                .upvotes(up)
                .downvotes(down)
                .status(s.status())
                .build();
    }

    private static String titleCaseWord(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        return Character.toUpperCase(raw.charAt(0)) + raw.substring(1).toLowerCase();
    }

    private List<SalaryResultResponse> sortResults(List<SalaryResultResponse> dtos, SalarySearchRequest request) {
        String sortBy = request.getSortBy();
        if (sortBy == null) {
            return dtos;
        }

        String sortField = sortBy.toLowerCase();
        boolean ascending = "asc".equalsIgnoreCase(request.getSortDir());

        return dtos.stream()
                .sorted((a, b) -> {
                    int compare = switch (sortField) {
                        case "upvotes" -> Integer.compare(a.getUpvotes(), b.getUpvotes());
                        case "downvotes" -> Integer.compare(a.getDownvotes(), b.getDownvotes());
                        default -> 0;
                    };
                    return ascending ? compare : -compare;
                })
                .toList();
    }
}
