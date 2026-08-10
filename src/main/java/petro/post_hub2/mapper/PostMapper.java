package petro.post_hub2.mapper;

import petro.post_hub2.model.dto.PostDto;
import petro.post_hub2.model.dto.PostSearchDto;
import petro.post_hub2.model.entity.Post;
import petro.post_hub2.model.request.NewPostRequest;


public interface PostMapper {
    PostDto convertPostToPostDto(Post post);

    Post convertPostToPostDto(NewPostRequest newPostRequest);

    PostSearchDto convertPostToPostSearchDto(Post post);


}
