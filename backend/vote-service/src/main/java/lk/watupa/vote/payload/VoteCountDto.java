package lk.watupa.vote.payload;

public record VoteCountDto(
        String submissionId,
        int upvoteCount,
        int downvoteCount
) {}
