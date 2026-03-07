package lk.watupa.identity.repository;

import lk.watupa.identity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByUserName(String username);

    Boolean existsByemail(String email);
}
