package petro.post_hub2.security;

import petro.post_hub2.model.dto.PostDto;
import petro.post_hub2.model.dto.PostSearchDto;
import petro.post_hub2.model.request.NewPostRequest;
import petro.post_hub2.model.request.PostSearchRequest;
import petro.post_hub2.model.request.UpdatePostRequest;
import petro.post_hub2.model.response.CustomResponse;
import petro.post_hub2.model.response.PaginationResponse;

import java.awt.print.Pageable;

public interface PostService {
    CustomResponse<PostDto> getById(int id);

    CustomResponse<PostDto> save(NewPostRequest newPostRequest);

    CustomResponse<PostDto> updated(UpdatePostRequest updatePostRequest, int postId, int id);

    void deleted(int id);

    CustomResponse<PaginationResponse<PostSearchDto>> findAllPostByPagination(Pageable pageable);

    CustomResponse<PaginationResponse<PostSearchDto>> findAllPostByPaginationAndParam(Pageable pageable, PostSearchRequest postSearchRequest);
}
