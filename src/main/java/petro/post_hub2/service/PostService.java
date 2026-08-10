package petro.post_hub2.service;

import org.springframework.data.domain.Pageable;
import petro.post_hub2.model.dto.PostDto;
import petro.post_hub2.model.dto.PostSearchDto;
import petro.post_hub2.model.request.NewPostRequest;
import petro.post_hub2.model.request.PostSearchRequest;
import petro.post_hub2.model.request.UpdatePostRequest;
import petro.post_hub2.model.response.CustomResponse;
import petro.post_hub2.model.response.PaginationResponse;


public interface PostService {
    CustomResponse<PostDto> getById(int id);

    CustomResponse<PostDto> save(NewPostRequest newPostRequest, int userId);

    CustomResponse<PostDto> updated(UpdatePostRequest updatePostRequest, int postId, int id);

    void deleted(int id, int userId);

    CustomResponse<PaginationResponse<PostSearchDto>> findAllPostsByPagination(Pageable pageable);

    CustomResponse<PaginationResponse<PostSearchDto>> findAllPostsByPaginationAndParams(Pageable pageable, PostSearchRequest postSearchRequest);
}
