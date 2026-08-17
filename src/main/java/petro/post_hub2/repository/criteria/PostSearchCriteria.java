package petro.post_hub2.repository.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;
import petro.post_hub2.model.entity.Post;
import petro.post_hub2.model.request.PostSearchRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class PostSearchCriteria implements Specification<Post> {
    private final PostSearchRequest postSearchRequest;


    @Override
    public @Nullable Predicate toPredicate(Root<Post> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        if (Objects.nonNull(postSearchRequest.getTitle())) {
            predicates.add(criteriaBuilder.like(root.get(Post.TITLE_FIELD_NAME), "%" + postSearchRequest.getTitle() + "%"));
        }
        if (Objects.nonNull(postSearchRequest.getContent())) {
            predicates.add(criteriaBuilder.like(root.get(Post.CONTENT_FIELD_NAME), "%" + postSearchRequest.getContent() + "%"));
        }
        if (Objects.nonNull(postSearchRequest.isDeleted())) {
            predicates.add(criteriaBuilder.equal(root.get(Post.DELETED_FIELD_NAME), postSearchRequest.isDeleted()));
        }
        if (Objects.nonNull(postSearchRequest.getKeyword())) {
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(root.get(Post.TITLE_FIELD_NAME), "%" + postSearchRequest.getKeyword() + "%"),
                    criteriaBuilder.like(root.get(Post.CONTENT_FIELD_NAME), "%" + postSearchRequest.getKeyword() + "%")

            ));
        }
        if (Objects.nonNull(postSearchRequest.getPostSortField())) {
            switch (postSearchRequest.getPostSortField()) {
                case TITLE -> query.orderBy(criteriaBuilder.desc(root.get(Post.TITLE_FIELD_NAME)));
                case CONTENT -> query.orderBy(criteriaBuilder.desc(root.get(Post.CONTENT_FIELD_NAME)));
                default -> query.orderBy(criteriaBuilder.desc(root.get(Post.ID_FIELD_NAME)));
            }
        } else {
            query.orderBy(criteriaBuilder.desc(root.get(Post.ID_FIELD_NAME)));
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}

