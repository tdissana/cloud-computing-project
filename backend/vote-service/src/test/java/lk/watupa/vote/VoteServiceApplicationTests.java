package lk.watupa.vote;

import lk.watupa.vote.enums.Status;
import lk.watupa.vote.enums.VoteType;
import lk.watupa.vote.model.Submission;
import lk.watupa.vote.model.VoteCount;
import lk.watupa.vote.payload.VoteResponse;
import lk.watupa.vote.repository.SubmissionRepository;
import lk.watupa.vote.repository.VoteCountRepository;
import lk.watupa.vote.repository.VoteRepository;
import lk.watupa.vote.service.VoteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class VoteServiceApplicationTests {

	@Autowired
	private VoteService voteService;

	@Autowired
	private VoteRepository voteRepository;

	@Autowired
	private VoteCountRepository voteCountRepository;

	@Autowired
	private SubmissionRepository submissionRepository;

	@BeforeEach
	void setUp() {
		voteRepository.deleteAll();
		voteCountRepository.deleteAll();
		submissionRepository.deleteAll();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void castVoteShouldCreateSingleVoteAndCountReference() {
		UUID submissionId = UUID.fromString("11111111-1111-1111-1111-111111111111");
		submissionRepository.save(new Submission(submissionId, Status.PENDING));
		VoteResponse response = voteService.castVote(submissionId, 10L, VoteType.UPVOTE);

		assertThat(response.getVoteType()).isEqualTo(VoteType.UPVOTE);
		assertThat(voteRepository.count()).isEqualTo(1);

		VoteCount count = voteCountRepository.findById(submissionId).orElseThrow();
		assertThat(count.getUpvoteCount()).isEqualTo(1);
		assertThat(count.getDownvoteCount()).isEqualTo(0);

		Submission submission = submissionRepository.findById(submissionId).orElseThrow();
		assertThat(submission.getStatus()).isEqualTo(Status.PENDING);
	}

	@Test
	void castingSameVoteTwiceShouldBeIdempotent() {
		UUID submissionId = UUID.fromString("22222222-2222-2222-2222-222222222222");
		submissionRepository.save(new Submission(submissionId, Status.PENDING));
		voteService.castVote(submissionId, 20L, VoteType.DOWNVOTE);
		voteService.castVote(submissionId, 20L, VoteType.DOWNVOTE);

		assertThat(voteRepository.count()).isEqualTo(1);

		VoteCount count = voteCountRepository.findById(submissionId).orElseThrow();
		assertThat(count.getUpvoteCount()).isEqualTo(0);
		assertThat(count.getDownvoteCount()).isEqualTo(1);
	}

	@Test
	void changingVoteTypeShouldUpdateReferenceCounts() {
		UUID submissionId = UUID.fromString("33333333-3333-3333-3333-333333333333");
		submissionRepository.save(new Submission(submissionId, Status.PENDING));
		voteService.castVote(submissionId, 30L, VoteType.UPVOTE);
		voteService.castVote(submissionId, 30L, VoteType.DOWNVOTE);

		assertThat(voteRepository.count()).isEqualTo(1);

		VoteCount count = voteCountRepository.findById(submissionId).orElseThrow();
		assertThat(count.getUpvoteCount()).isEqualTo(0);
		assertThat(count.getDownvoteCount()).isEqualTo(1);
	}

	@Test
	void shouldApproveSubmissionWhenThresholdIsReached() {
		UUID submissionId = UUID.fromString("44444444-4444-4444-4444-444444444444");
		submissionRepository.save(new Submission(submissionId, Status.PENDING));

		voteService.castVote(submissionId, 40L, VoteType.UPVOTE);
		voteService.castVote(submissionId, 41L, VoteType.DOWNVOTE);

		Submission submission = submissionRepository.findById(submissionId).orElseThrow();
		assertThat(submission.getStatus()).isEqualTo(Status.APPROVED);
	}

}
