package petro.post_hub2.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import petro.post_hub2.model.dto.PostDto;
import petro.post_hub2.model.dto.PostSearchDto;
import petro.post_hub2.model.entity.Post;
import petro.post_hub2.model.request.NewPostRequest;

import java.awt.print.Pageable;

@Mapper(
        componentModel = "string",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports = {Object.class}
)
public interface PostMapper {
    PostDto convertPostToPostDto(Post post);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Post convertPostToPostDto(NewPostRequest newPostRequest);

    PostSearchDto convertPostToPostSearchDto(Pageable pageable);


}
