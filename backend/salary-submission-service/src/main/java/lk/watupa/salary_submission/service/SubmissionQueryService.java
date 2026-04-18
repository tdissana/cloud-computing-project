package lk.watupa.salary_submission.service;

import lk.watupa.salary_submission.enums.Status;
import lk.watupa.salary_submission.model.Submission;
import lk.watupa.salary_submission.payload.*;
import lk.watupa.salary_submission.repository.SubmissionRepository;
import lk.watupa.salary_submission.repository.SubmissionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionQueryService {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final SubmissionRepository repository;

    @Transactional(readOnly = true)
    public PagedDto<SubmissionDto> search(SubmissionSearchRequest request) {
        Pageable pageable = buildPageable(request);
        Page<Submission> page = repository.findAll(
                SubmissionSpecification.fromRequest(request), pageable);

        List<SubmissionDto> dtos = page.getContent().stream()
                .map(SubmissionDto::from)
                .toList();

        return new PagedDto<>(
                dtos,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    @Transactional(readOnly = true)
    public FilterOptionsDto getFilterOptions() {
        return new FilterOptionsDto(
                repository.findDistinctCountries(),
                repository.findDistinctCompanies(),
                repository.findDistinctJobTitles(),
                repository.findDistinctExperienceLevels(),
                repository.findDistinctCurrencies()
        );
    }

    @Transactional
    public SubmissionDto updateStatus(Long id, String status) {
        Submission submission = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found: " + id));

        Status newStatus = Status.valueOf(status.toUpperCase());
        submission.setStatus(newStatus);
        repository.save(submission);

        return SubmissionDto.from(submission);
    }

    private Pageable buildPageable(SubmissionSearchRequest req) {
        int page = (req.page() != null && req.page() >= 0) ? req.page() : 0;
        int size = (req.size() != null && req.size() > 0)
                ? Math.min(req.size(), MAX_PAGE_SIZE)
                : DEFAULT_PAGE_SIZE;

        String sortField = mapSortField(req.sortBy());

        Sort.Direction dir = "asc".equalsIgnoreCase(req.sortDir())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return PageRequest.of(page, size, Sort.by(dir, sortField));
    }

    private String mapSortField(String sortBy) {
        if (sortBy == null) return "timestamp";
        return switch (sortBy.toLowerCase()) {
            case "approvedat", "timestamp" -> "timestamp";
            case "grossmonthlysalary", "basesalary" -> "baseSalary";
            case "yearsofexperience", "seniority" -> "seniority";
            case "totalcompensation" -> "totalCompensation";
            default -> "timestamp";
        };
    }
}
