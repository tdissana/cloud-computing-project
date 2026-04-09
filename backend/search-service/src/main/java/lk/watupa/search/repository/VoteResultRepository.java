package lk.watupa.search.repository;

import lk.watupa.search.model.VoteResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface VoteResultRepository extends JpaRepository<VoteResult, String> {

    List<VoteResult> findByIdIn(Collection<String> ids);
}
