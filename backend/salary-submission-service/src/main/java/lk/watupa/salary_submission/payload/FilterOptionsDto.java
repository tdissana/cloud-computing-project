package lk.watupa.salary_submission.payload;

import java.util.List;

public record FilterOptionsDto(
        List<String> countries,
        List<String> companies,
        List<String> jobTitles,
        List<String> experienceLevels,
        List<String> currencies
) {}
