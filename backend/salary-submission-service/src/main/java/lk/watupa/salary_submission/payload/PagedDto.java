package lk.watupa.salary_submission.payload;

import java.util.List;

public record PagedDto<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {}
