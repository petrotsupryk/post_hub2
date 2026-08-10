package petro.post_hub2.mapper.impl;

import org.springframework.stereotype.Component;
import petro.post_hub2.mapper.PostMapper;
import petro.post_hub2.model.dto.PostDto;
import petro.post_hub2.model.dto.PostSearchDto;
import petro.post_hub2.model.entity.Post;
import petro.post_hub2.model.request.NewPostRequest;

@Component
public class PostMapperImpl implements PostMapper {
    @Override
    public PostDto convertPostToPostDto(Post post) {
        return new PostDto(post.getId(), post.getTitle(), post.getContent(), post.getCreated(), post.getUpdated());
    }

    @Override
    public Post convertPostToPostDto(NewPostRequest newPostRequest) {
        Post post = new Post();
        post.setTitle(newPostRequest.getTitle());
        post.setContent(newPostRequest.getContent());
        return post;
    }

    @Override
    public PostSearchDto convertPostToPostSearchDto(Post post) {
        return new PostSearchDto(post.getId(), post.getTitle(), post.getContent(), post.getCreated(),
                post.getUpdated(), post.isDeleted());
    }
}
