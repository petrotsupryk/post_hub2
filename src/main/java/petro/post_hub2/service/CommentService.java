package petro.post_hub2.service;

import org.springframework.data.domain.Pageable;
import petro.post_hub2.model.dto.CommentDto;
import petro.post_hub2.model.dto.CommentSearchDto;
import petro.post_hub2.model.request.CommentSearchRequest;
import petro.post_hub2.model.request.NewCommentRequest;
import petro.post_hub2.model.request.UpdateCommentRequest;
import petro.post_hub2.model.response.CustomResponse;
import petro.post_hub2.model.response.PaginationResponse;

public interface CommentService {
    CustomResponse<CommentDto> getCommentById(int id);

    CustomResponse<CommentDto> createComment(NewCommentRequest newCommentRequest, int userId);

    CustomResponse<CommentDto> updateComment(int id, UpdateCommentRequest updateCommentRequest, int userId);

    void delete(int id, int userId);

    CustomResponse<PaginationResponse<CommentSearchDto>> findAllCommentWithPagination(Pageable pageable);

    CustomResponse<PaginationResponse<CommentSearchDto>> findAllCommentWithPaginationAndParams(CommentSearchRequest commentSearchRequest, Pageable pageable);
}
