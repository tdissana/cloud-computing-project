package lk.watupa.salary_submission_service.repository;

import lk.watupa.salary_submission_service.payload.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface submissionRepository extends JpaRepository<Submission,Long > {

}
