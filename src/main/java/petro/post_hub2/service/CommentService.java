package petro.post_hub2.service;

import petro.post_hub2.model.dto.CommentDto;
import petro.post_hub2.model.request.NewCommentRequest;
import petro.post_hub2.model.response.CustomResponse;

public interface CommentService {
    CustomResponse<CommentDto>getCommentById(int id);
    //CustomResponse<CommentDto>createComment(NewCommentRequest newCommentRequest, int postId);
}
