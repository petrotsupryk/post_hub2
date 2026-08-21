package petro.post_hub2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import petro.post_hub2.model.entity.Comment;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer>, JpaSpecificationExecutor<Comment> {
    Optional<Comment> findByIdAndDeletedFalse(int id);
}
