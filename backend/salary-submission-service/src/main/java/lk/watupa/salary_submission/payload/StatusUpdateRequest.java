package lk.watupa.salary_submission.payload;

import jakarta.validation.constraints.NotBlank;

public record StatusUpdateRequest(
        @NotBlank String status
) {}
