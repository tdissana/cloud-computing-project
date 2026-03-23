package lk.watupa.vote;

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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class VoteServiceApplicationTests {

	@Autowired
	private VoteService voteService;

	@Autowired
	private VoteRepository voteRepository;

	@Autowired
	private VoteCountRepository voteCountRepository;

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
		VoteResponse response = voteService.castVote(100L, 10L, VoteType.UPVOTE);

		assertThat(response.getVoteType()).isEqualTo(VoteType.UPVOTE);
		assertThat(voteRepository.count()).isEqualTo(1);

		VoteCount count = voteCountRepository.findById(100L).orElseThrow();
		assertThat(count.getUpvoteCount()).isEqualTo(1);
		assertThat(count.getDownvoteCount()).isEqualTo(0);
	}

	@Test
	void castingSameVoteTwiceShouldBeIdempotent() {
		voteService.castVote(200L, 20L, VoteType.DOWNVOTE);
		voteService.castVote(200L, 20L, VoteType.DOWNVOTE);

		assertThat(voteRepository.count()).isEqualTo(1);

		VoteCount count = voteCountRepository.findById(200L).orElseThrow();
		assertThat(count.getUpvoteCount()).isEqualTo(0);
		assertThat(count.getDownvoteCount()).isEqualTo(1);
	}

	@Test
	void changingVoteTypeShouldUpdateReferenceCounts() {
		voteService.castVote(300L, 30L, VoteType.UPVOTE);
		voteService.castVote(300L, 30L, VoteType.DOWNVOTE);

		assertThat(voteRepository.count()).isEqualTo(1);

		VoteCount count = voteCountRepository.findById(300L).orElseThrow();
		assertThat(count.getUpvoteCount()).isEqualTo(0);
		assertThat(count.getDownvoteCount()).isEqualTo(1);
	}

}
