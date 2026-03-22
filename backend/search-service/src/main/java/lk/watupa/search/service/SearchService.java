package lk.watupa.search.service;

import lk.watupa.search.model.ApprovedSalary;
import lk.watupa.search.model.VoteResult;
import lk.watupa.search.payload.*;
import lk.watupa.search.repository.SalarySearchRepository;
import lk.watupa.search.repository.SalarySpecification;
import lk.watupa.search.repository.VoteResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SearchService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;
    private static final List<String> EMPLOYMENT_TYPES = List.of(
            "Full-time", "Part-time", "Contract", "Freelance"
    );

    private static final DateTimeFormatter APPROVED_AT_FMT =
            DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);

    private final SalarySearchRepository repository;
    private final VoteResultRepository voteResultRepository;

    public PagedResponse<SalaryResultResponse> search(SalarySearchRequest request) {
        Pageable pageable = buildPageable(request);
        Page<ApprovedSalary> page = repository.findAll(
                SalarySpecification.fromRequest(request), pageable);

        List<ApprovedSalary> rows = page.getContent();
        List<String> idStrings = rows.stream()
                .map(s -> String.valueOf(s.getId()))
                .toList();

        Map<String, VoteResult> votesById = voteResultRepository.findByIdIn(idStrings).stream()
                .collect(Collectors.toMap(VoteResult::getId, v -> v, (a, b) -> a));

        List<SalaryResultResponse> dtos = rows.stream()
                .map(s -> toDto(s, votesById.get(String.valueOf(s.getId()))))
                .toList();

        // Apply in-memory sorting for vote-based sorts
        List<SalaryResultResponse> sortedDtos = sortResults(dtos, request);

        log.info("Search returned {} results (page {}/{}) verificationStatus={}",
                sortedDtos.size(), page.getNumber(), page.getTotalPages(),
                request.getVerificationStatus());

        return PagedResponse.<SalaryResultResponse>builder()
                .content(sortedDtos)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    public FilterOptionsResponse getFilterOptions() {
        return FilterOptionsResponse.builder()
                .countries(repository.findDistinctCountries())
                .companies(repository.findDistinctCompanies())
                .jobTitles(repository.findDistinctJobTitles())
                .seniorityLevels(repository.findDistinctExperienceLevels())
                .employmentTypes(EMPLOYMENT_TYPES)
                .currencies(repository.findDistinctCurrencies())
                .build();
    }

    private SalaryResultResponse toDto(ApprovedSalary s, VoteResult vote) {
        boolean anon = Boolean.TRUE.equals(s.getAnonymize());
        int up = vote != null ? vote.getUpVoteCount() : 0;
        int down = vote != null ? vote.getDownVoteCount() : 0;

        Double gross = s.getBaseSalary();
        Double additional = null;
        if (s.getTotalCompensation() != null && s.getBaseSalary() != null) {
            double diff = s.getTotalCompensation() - s.getBaseSalary();
            if (diff > 0) {
                additional = diff;
            }
        }

        String approvedAt = s.getTimestamp() == null
                ? null
                : APPROVED_AT_FMT.format(s.getTimestamp().atOffset(ZoneOffset.UTC));

        String level = s.getExperienceLevel() == null
                ? ""
                : titleCaseWord(s.getExperienceLevel());

        return SalaryResultResponse.builder()
                .id(String.valueOf(s.getId()))
                .companyName(anon ? "Anonymous" : s.getCompanyName())
                .jobTitle(s.getJobTitle())
                .seniorityLevel(level)
                .employmentType("Full-time")
                .country(s.getCountry())
                .city(anon ? null : null)
                .grossMonthlySalary(gross)
                .currency(s.getCurrency())
                .additionalCompensation(additional)
                .yearsOfExperience(s.getSeniority())
                .techStack(s.getSkills() != null ? s.getSkills() : "")
                .anonymized(anon)
                .approvedAt(approvedAt != null ? approvedAt : "")
                .upvotes(up)
                .downvotes(down)
                .status(s.getStatus())
                .build();
    }

    private static String titleCaseWord(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        return Character.toUpperCase(raw.charAt(0)) + raw.substring(1).toLowerCase();
    }

    private Pageable buildPageable(SalarySearchRequest req) {
        int page = (req.getPage() != null && req.getPage() >= 0) ? req.getPage() : 0;
        int size = (req.getSize() != null && req.getSize() > 0)
                ? Math.min(req.getSize(), MAX_PAGE_SIZE)
                : DEFAULT_PAGE_SIZE;

        String sortField = mapSortToEntityProperty(req.getSortBy());

        Sort.Direction dir = "asc".equalsIgnoreCase(req.getSortDir())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return PageRequest.of(page, size, Sort.by(dir, sortField));
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
                        default -> 0;  // No in-memory sort for DB fields
                    };
                    return ascending ? compare : -compare;
                })
                .toList();
    }

    private String mapSortToEntityProperty(String sortBy) {
        if (sortBy == null) {
            return "timestamp";
        }
        String s = sortBy.toLowerCase();
        return switch (s) {
            case "approvedat" -> "timestamp";
            case "grossmonthlysalary" -> "baseSalary";
            case "yearsofexperience" -> "seniority";
            case "upvotes", "downvotes" -> "timestamp";  // Ignored for DB, sorted in-memory
            case "basesalary" -> "baseSalary";
            case "totalcompensation" -> "totalCompensation";
            case "seniority" -> "seniority";
            case "timestamp" -> "timestamp";
            default -> "timestamp";
        };
    }
}
