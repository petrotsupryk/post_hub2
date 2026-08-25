package petro.post_hub2.mapper.impl;

import petro.post_hub2.mapper.CommentMapper;
import petro.post_hub2.model.dto.CommentDto;
import petro.post_hub2.model.dto.CommentSearchDto;
import petro.post_hub2.model.entity.Comment;
import petro.post_hub2.model.entity.Post;
import petro.post_hub2.model.entity.User;
import petro.post_hub2.model.request.NewCommentRequest;

public class CommentMapperImpl implements CommentMapper {
    @Override
    public CommentDto convertCommentToCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getMessage(), comment.getUser().getId(), comment.getPost().getId(),
                comment.getCreated(), comment.getUpdated());
    }

    @Override
    public Comment convertNewCommentRequestToComment(NewCommentRequest newCommentRequest, User user, Post post) {
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setMessage(newCommentRequest.getMessage());
        return comment;
    }

    @Override
    public CommentSearchDto convertCommentToCommentSearchDto(Comment comment) {
        return new CommentSearchDto(comment.getId(), comment.getUser().getId(), comment.getPost().getId(),
                comment.getCreated(), comment.getUpdated(), comment.isDeleted());
    }
}
