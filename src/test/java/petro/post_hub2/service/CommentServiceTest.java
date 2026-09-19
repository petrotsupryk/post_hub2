package petro.post_hub2.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import petro.post_hub2.mapper.CommentMapper;
import petro.post_hub2.model.constants.ApiErrorMessage;
import petro.post_hub2.model.dto.CommentDto;
import petro.post_hub2.model.entity.Comment;
import petro.post_hub2.model.entity.Post;
import petro.post_hub2.model.entity.User;
import petro.post_hub2.model.exceptions.NotFoundException;
import petro.post_hub2.repository.CommentRepository;
import petro.post_hub2.repository.PostRepository;
import petro.post_hub2.repository.UserRepository;
import petro.post_hub2.service.impl.CommentServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private CommentServiceImpl commentServiceImpl;

    private User testUser;
    private Post testPost;
    private Comment testComment;
    private CommentDto testCommentDto;

    @BeforeEach
    void up() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testUserName");

        testPost = new Post();
        testPost.setId(1);
        testPost.setTitle("testTitle");

        testComment = new Comment();
        testComment.setId(1);
        testComment.setMessage("testMessage");
        testComment.setUser(testUser);
        testComment.setPost(testPost);

        testCommentDto = new CommentDto();
        testCommentDto.setId(1);
        testCommentDto.setMessage("testMessage");
    }

    @Test
    void getByIdComment_Ok() {
        when(commentRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testComment));
        when(commentMapper.convertCommentToCommentDto(testComment)).thenReturn(testCommentDto);

        CommentDto response = commentServiceImpl.getCommentById(1).getBody();

        assertNotNull(response);

        assertEquals(testCommentDto.getId(), response.getId());
        assertEquals(testCommentDto.getMessage(), response.getMessage());

        verify(commentRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(commentMapper, times(1)).convertCommentToCommentDto(testComment);
    }

    @Test
    void getByIdComment_NotFound_NotFoundException() {
        when(commentRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                commentServiceImpl.getCommentById(1));

        assertEquals(ApiErrorMessage.COOMENT_NOT_FOUND_BY_ID.getMessage(1), exception.getMessage());

        verify(commentRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(commentMapper, never()).convertCommentToCommentDto(testComment);
    }
}
