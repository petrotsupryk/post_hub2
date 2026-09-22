package petro.post_hub2.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import petro.post_hub2.mapper.CommentMapper;
import petro.post_hub2.model.constants.ApiErrorMessage;
import petro.post_hub2.model.dto.CommentDto;
import petro.post_hub2.model.entity.Comment;
import petro.post_hub2.model.entity.Post;
import petro.post_hub2.model.entity.User;
import petro.post_hub2.model.exceptions.NotFoundException;
import petro.post_hub2.model.request.NewCommentRequest;
import petro.post_hub2.model.request.UpdateCommentRequest;
import petro.post_hub2.repository.CommentRepository;
import petro.post_hub2.repository.PostRepository;
import petro.post_hub2.repository.UserRepository;
import petro.post_hub2.security.validation.AccessValidator;
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
    @Mock
    private AccessValidator accessValidator;
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

    @Test
    void createComment_Ok() {
        NewCommentRequest request = new NewCommentRequest(1, "new comment");

        when(userRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testUser));
        when(postRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testPost));
        when(commentMapper.convertNewCommentRequestToComment(eq(request), any(User.class), any(Post.class))).thenReturn(testComment);
        when(commentRepository.save(testComment)).thenReturn(testComment);
        when(commentMapper.convertCommentToCommentDto(testComment)).thenReturn(testCommentDto);

        CommentDto result = commentServiceImpl.createComment(request, testUser.getId()).getBody();

        assertNotNull(result);

        assertEquals(testCommentDto.getId(), result.getId());
        assertEquals(testCommentDto.getMessage(), result.getMessage());

        verify(userRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(postRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(commentMapper, times(1)).convertNewCommentRequestToComment(eq(request),
                any(User.class), any(Post.class));
        verify(commentRepository, times(1)).save(testComment);
        verify(commentMapper, times(1)).convertCommentToCommentDto(testComment);
    }

    @Test
    void createComment_UserNotFound_NotFoundException() {
        NewCommentRequest request = new NewCommentRequest(1, "new comment");

        when(userRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                commentServiceImpl.createComment(request, testUser.getId()));
        assertEquals(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(testUser.getId()), exception.getMessage());

        verify(userRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(postRepository, never()).findByIdAndDeletedFalse(1);
        verify(commentMapper, never()).convertNewCommentRequestToComment(eq(request), any(User.class), any(Post.class));
        verify(commentRepository, never()).save(testComment);
        verify(commentMapper, never()).convertCommentToCommentDto(testComment);
    }

    @Test
    void createComment_PostNotFound_NotFoundException() {
        NewCommentRequest request = new NewCommentRequest(1, "new comment");

        when(userRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testUser));
        when(postRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                commentServiceImpl.createComment(request, testUser.getId()));
        assertEquals(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(request.getPostId()), exception.getMessage());

        verify(userRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(postRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(commentMapper, never()).convertNewCommentRequestToComment(eq(request), any(User.class), any(Post.class));
        verify(commentRepository, never()).save(testComment);
        verify(commentMapper, never()).convertCommentToCommentDto(testComment);
    }

    @Test
    void updateComment_Ok() {
        UpdateCommentRequest request = new UpdateCommentRequest("update comment");

        when(commentRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testComment));
        doNothing().when(accessValidator).validateAdminOrOwnerAccess(testComment.getUser().getId(), testUser.getId());
        when(commentRepository.save(testComment)).thenReturn(testComment);
        when(commentMapper.convertCommentToCommentDto(testComment)).thenReturn(testCommentDto);

        CommentDto result = commentServiceImpl.updateComment(1, request, testUser.getId()).getBody();

        assertNotNull(result);

        assertEquals(testCommentDto.getId(), result.getId());
        assertEquals(testCommentDto.getMessage(), result.getMessage());

        verify(commentRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(accessValidator, times(1)).validateAdminOrOwnerAccess(testComment.getUser().getId(), testUser.getId());
        verify(commentRepository, times(1)).save(testComment);
        verify(commentMapper, times(1)).convertCommentToCommentDto(testComment);
    }

    @Test
    void updateComment_NotfoundComment_NotFoundException() {
        UpdateCommentRequest request = new UpdateCommentRequest("update comment");

        when(commentRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                commentServiceImpl.updateComment(1, request, testUser.getId()));

        assertEquals(ApiErrorMessage.COOMENT_NOT_FOUND_BY_ID.getMessage(1), exception.getMessage());

        verify(commentRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(accessValidator, never()).validateAdminOrOwnerAccess(testComment.getUser().getId(), testUser.getId());
        verify(commentRepository, never()).save(testComment);
        verify(commentMapper, never()).convertCommentToCommentDto(testComment);
    }

    @Test
    void Updated_ValidateAdminOrOwnerAccess_AccessDeniedException() {
        UpdateCommentRequest request = new UpdateCommentRequest("update comment");

        when(commentRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testComment));
        doThrow(new AccessDeniedException(ApiErrorMessage.DONT_HAVE_ACCESS.getMessage()))
                .when(accessValidator)
                .validateAdminOrOwnerAccess(testComment.getUser().getId(), testUser.getId());

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                commentServiceImpl.updateComment(1, request, testUser.getId()));

        assertEquals(ApiErrorMessage.DONT_HAVE_ACCESS.getMessage(1), exception.getMessage());

        verify(commentRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(accessValidator, times(1)).validateAdminOrOwnerAccess(testComment.getUser().getId(), testUser.getId());
        verify(commentRepository, never()).save(testComment);
        verify(commentMapper, never()).convertCommentToCommentDto(testComment);
    }

    @Test
    void deleted_Ok() {
        when(commentRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testComment));
        doNothing().when(accessValidator).validateAdminOrOwnerAccess(testComment.getUser().getId(), testUser.getId());
        when(commentRepository.save(testComment)).thenReturn(testComment);

        commentServiceImpl.delete(1, testUser.getId());

        verify(commentRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(accessValidator, times(1)).validateAdminOrOwnerAccess(testComment.getUser().getId(), testUser.getId());
        verify(commentRepository, times(1)).save(testComment);
    }

    @Test
    void deletedComment_NotFound_NotFoundException() {
        when(commentRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                commentServiceImpl.delete(1, testUser.getId()));

        assertEquals(ApiErrorMessage.COOMENT_NOT_FOUND_BY_ID.getMessage(1), exception.getMessage());

        verify(commentRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(accessValidator, never()).validateAdminOrOwnerAccess(testComment.getUser().getId(), testUser.getId());
        verify(commentRepository, never()).save(testComment);
    }

    @Test
    void deletedComment_ValidateAdminOrOwnerAccess_AccessDeniedException() {
        when(commentRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testComment));
        doThrow(new AccessDeniedException(ApiErrorMessage.DONT_HAVE_ACCESS.getMessage()))
                .when(accessValidator).validateAdminOrOwnerAccess(testComment.getUser().getId(), testUser.getId());

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                commentServiceImpl.delete(1, testUser.getId()));

        assertEquals(ApiErrorMessage.DONT_HAVE_ACCESS.getMessage(1), exception.getMessage());

        verify(commentRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(accessValidator, times(1)).validateAdminOrOwnerAccess(testComment.getUser().getId(), testUser.getId());
        verify(commentRepository, never()).save(testComment);
    }
}
