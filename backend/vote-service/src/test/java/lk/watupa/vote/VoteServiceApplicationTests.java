package lk.watupa.vote;

import lk.watupa.vote.client.SalarySubmissionClient;
import lk.watupa.vote.enums.VoteType;
import lk.watupa.vote.model.VoteCount;
import lk.watupa.vote.payload.VoteResponse;
import lk.watupa.vote.repository.VoteCountRepository;
import lk.watupa.vote.repository.VoteRepository;
import lk.watupa.vote.service.VoteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class VoteServiceApplicationTests {

	@Autowired
	private VoteService voteService;

	@Autowired
	private VoteRepository voteRepository;

	@Autowired
	private VoteCountRepository voteCountRepository;

	@MockitoBean
	private SalarySubmissionClient salarySubmissionClient;

	@BeforeEach
	void setUp() {
		voteRepository.deleteAll();
		voteCountRepository.deleteAll();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void castVoteShouldCreateSingleVoteAndCountReference() {
		Long submissionId = 1001L;
		VoteResponse response = voteService.castVote(submissionId, 10L, VoteType.UPVOTE);

		assertThat(response.getVoteType()).isEqualTo(VoteType.UPVOTE);
		assertThat(voteRepository.count()).isEqualTo(1);

		VoteCount count = voteCountRepository.findById(submissionId).orElseThrow();
		assertThat(count.getUpvoteCount()).isEqualTo(1);
		assertThat(count.getDownvoteCount()).isEqualTo(0);
	}

	@Test
	void castingSameVoteTwiceShouldBeIdempotent() {
		Long submissionId = 2002L;
		voteService.castVote(submissionId, 20L, VoteType.DOWNVOTE);
		voteService.castVote(submissionId, 20L, VoteType.DOWNVOTE);

		assertThat(voteRepository.count()).isEqualTo(1);

		VoteCount count = voteCountRepository.findById(submissionId).orElseThrow();
		assertThat(count.getUpvoteCount()).isEqualTo(0);
		assertThat(count.getDownvoteCount()).isEqualTo(1);
	}

	@Test
	void changingVoteTypeShouldUpdateReferenceCounts() {
		Long submissionId = 3003L;
		voteService.castVote(submissionId, 30L, VoteType.UPVOTE);
		voteService.castVote(submissionId, 30L, VoteType.DOWNVOTE);

		assertThat(voteRepository.count()).isEqualTo(1);

		VoteCount count = voteCountRepository.findById(submissionId).orElseThrow();
		assertThat(count.getUpvoteCount()).isEqualTo(0);
		assertThat(count.getDownvoteCount()).isEqualTo(1);
	}

	@Test
	void shouldCallSalaryServiceWhenThresholdIsReached() {
		Long submissionId = 4004L;

		voteService.castVote(submissionId, 40L, VoteType.UPVOTE);
		voteService.castVote(submissionId, 41L, VoteType.DOWNVOTE);

		// Threshold is 2 in test config — salary-submission-service should be called
		org.mockito.Mockito.verify(salarySubmissionClient)
				.updateStatus(submissionId.toString(), "APPROVED");
	}

}
