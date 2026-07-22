package petro.post_hub2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import petro.post_hub2.model.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<User> findByIdAndDeletedFalse(int id);

    Optional<User> findByEmailAndDeletedFalse(String email);
}
