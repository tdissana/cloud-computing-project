package lk.watupa.search.payload;

import java.util.List;

public record OwnerFilterOptionsDto(
        List<String> countries,
        List<String> companies,
        List<String> jobTitles,
        List<String> experienceLevels,
        List<String> currencies
) {}
