package lk.watupa.search_service.service;

import lk.watupa.search_service.dto.*;
import lk.watupa.search_service.model.ApprovedSalary;
import lk.watupa.search_service.repository.SalarySearchRepository;
import lk.watupa.search_service.repository.SalarySpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SearchService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "grossMonthlySalary", "yearsOfExperience", "approvedAt", "upvotes"
    );

    private final SalarySearchRepository repository;

    /**
     * Execute a filtered, paginated salary search.
     * Anonymized records have company name and city masked in the response.
     */
    public PagedResponse<SalaryResultDto> search(SalarySearchRequest request) {
        Pageable pageable = buildPageable(request);
        Page<ApprovedSalary> page = repository.findAll(
                SalarySpecification.fromRequest(request), pageable);

        List<SalaryResultDto> dtos = page.getContent().stream()
                .map(this::toDto)
                .toList();

        log.info("Search returned {} results (page {}/{})",
                dtos.size(), page.getNumber(), page.getTotalPages());

        return PagedResponse.<SalaryResultDto>builder()
                .content(dtos)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    /**
     * Return distinct filter values so the frontend can populate dropdowns.
     */
    public FilterOptionsDto getFilterOptions() {
        return FilterOptionsDto.builder()
                .countries(repository.findDistinctCountries())
                .companies(repository.findDistinctCompanies())
                .jobTitles(repository.findDistinctJobTitles())
                .seniorityLevels(repository.findDistinctSeniorityLevels())
                .employmentTypes(repository.findDistinctEmploymentTypes())
                .currencies(repository.findDistinctCurrencies())
                .build();
    }

    // -------------------------------------------------------------------------
    // Mapping helpers
    // -------------------------------------------------------------------------

    /**
     * Map entity to DTO and apply anonymization masking.
     * When anonymized=true:  companyName → "Anonymous", city → null
     */
    private SalaryResultDto toDto(ApprovedSalary salary) {
        boolean anon = Boolean.TRUE.equals(salary.getAnonymized());
        return SalaryResultDto.builder()
                .id(salary.getId())
                .companyName(anon ? "Anonymous" : salary.getCompanyName())
                .jobTitle(salary.getJobTitle())
                .seniorityLevel(salary.getSeniorityLevel())
                .employmentType(salary.getEmploymentType())
                .country(salary.getCountry())
                .city(anon ? null : salary.getCity())
                .grossMonthlySalary(salary.getGrossMonthlySalary())
                .currency(salary.getCurrency())
                .additionalCompensation(salary.getAdditionalCompensation())
                .yearsOfExperience(salary.getYearsOfExperience())
                .techStack(salary.getTechStack())
                .anonymized(anon)
                .approvedAt(salary.getApprovedAt())
                .upvotes(salary.getUpvotes())
                .downvotes(salary.getDownvotes())
                .build();
    }

    private Pageable buildPageable(SalarySearchRequest req) {
        int page = (req.getPage() != null && req.getPage() >= 0) ? req.getPage() : 0;
        int size = (req.getSize() != null && req.getSize() > 0)
                ? Math.min(req.getSize(), MAX_PAGE_SIZE)
                : DEFAULT_PAGE_SIZE;

        String sortField = (req.getSortBy() != null && SORTABLE_FIELDS.contains(req.getSortBy()))
                ? req.getSortBy()
                : "approvedAt";

        Sort.Direction dir = "asc".equalsIgnoreCase(req.getSortDir())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return PageRequest.of(page, size, Sort.by(dir, sortField));
    }
}