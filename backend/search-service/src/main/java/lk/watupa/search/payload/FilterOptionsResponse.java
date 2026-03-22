package lk.watupa.search.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Distinct filter values for populating dropdowns in the frontend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterOptionsResponse {
    private List<String> countries;
    private List<String> companies;
    private List<String> jobTitles;
    private List<String> seniorityLevels;
    private List<String> employmentTypes;
    private List<String> currencies;
}
