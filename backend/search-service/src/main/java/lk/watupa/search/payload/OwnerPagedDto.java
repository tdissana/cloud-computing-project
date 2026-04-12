package lk.watupa.search.payload;

import java.util.List;

public record OwnerPagedDto<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {}
