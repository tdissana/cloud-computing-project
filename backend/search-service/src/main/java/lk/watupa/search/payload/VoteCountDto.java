package lk.watupa.search.payload;

public record VoteCountDto(
        String submissionId,
        int upvoteCount,
        int downvoteCount
) {}
