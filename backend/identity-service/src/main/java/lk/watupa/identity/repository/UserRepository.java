package lk.watupa.identity.repository;

import lk.watupa.identity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByUserName(String username);

    Boolean existsByemail(String email);

    Optional<User> findByUserName(String username);
}
