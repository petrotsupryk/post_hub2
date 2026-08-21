package petro.post_hub2.repository.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;
import petro.post_hub2.model.entity.Comment;
import petro.post_hub2.model.request.CommentSearchRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class CommentSearchCriteria implements Specification<Comment> {
    private final CommentSearchRequest commentSearchRequest;

    @Override
    public @Nullable Predicate toPredicate(Root<Comment> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        if (Objects.nonNull(commentSearchRequest.getMessage())) {
            predicates.add(criteriaBuilder.like(root.get(Comment.MESSAGE_FIELD_NAME), "%" + commentSearchRequest.getMessage() + "%"));
        }
        if (Objects.nonNull(commentSearchRequest.isDeleted())) {
            predicates.add(criteriaBuilder.equal(root.get(Comment.DELETED_FIELD_NAME), commentSearchRequest.isDeleted()));
        }
        if (Objects.nonNull(commentSearchRequest.getSortField())) {
            switch (commentSearchRequest.getSortField()) {
                case MESSAGE -> query.orderBy(criteriaBuilder.desc(root.get(Comment.MESSAGE_FIELD_NAME)));
                default -> query.orderBy(criteriaBuilder.desc(root.get(Comment.ID_FIELD_NAME)));
            }
        } else {
            query.orderBy(criteriaBuilder.desc(root.get(Comment.ID_FIELD_NAME)));
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
