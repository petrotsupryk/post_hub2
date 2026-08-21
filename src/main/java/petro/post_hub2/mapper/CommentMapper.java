package petro.post_hub2.mapper;

import petro.post_hub2.model.dto.CommentDto;
import petro.post_hub2.model.dto.CommentSearchDto;
import petro.post_hub2.model.entity.Comment;
import petro.post_hub2.model.entity.Post;
import petro.post_hub2.model.entity.User;
import petro.post_hub2.model.request.NewCommentRequest;

public interface CommentMapper {
    CommentDto convertCommentToCommentDto(Comment comment);

    Comment convertNewCommentRequestToComment(NewCommentRequest newCommentRequest, User user, Post post);

    CommentSearchDto convertCommentToCommentSearchDto(Comment comment);
}
