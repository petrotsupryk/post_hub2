package petro.post_hub2.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import petro.post_hub2.mapper.PostMapper;
import petro.post_hub2.model.constants.ApiErrorMessage;
import petro.post_hub2.model.dto.PostDto;
import petro.post_hub2.model.entity.Post;
import petro.post_hub2.model.entity.User;
import petro.post_hub2.model.exceptions.DataExistsException;
import petro.post_hub2.model.exceptions.NotFoundException;
import petro.post_hub2.model.request.NewPostRequest;
import petro.post_hub2.model.request.UpdatePostRequest;
import petro.post_hub2.repository.PostRepository;
import petro.post_hub2.repository.UserRepository;
import petro.post_hub2.security.validation.AccessValidator;
import petro.post_hub2.service.impl.PostServiceImpl;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostMapper postMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AccessValidator accessValidator;
    @InjectMocks
    private PostServiceImpl postServiceImpl;

    private Post testPost;
    private PostDto testPostDto;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testUser");

        testPost = new Post();
        testPost.setId(1);
        testPost.setTitle("testTitle");
        testPost.setContent("testContent");
        testPost.setUser(testUser);

        testPostDto = new PostDto();
        testPostDto.setId(1);
        testPostDto.setTitle("testTitle");
        testPostDto.setContent("testContent");

    }

    @Test
    void getById_PostExists_ReturnsPostDto() {
        when(postRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testPost));
        when(postMapper.convertPostToPostDto(testPost)).thenReturn(testPostDto);

        PostDto result = postServiceImpl.getById(1).getBody();

        assertNotNull(result);

        assertEquals(testPostDto.getId(), result.getId());
        assertEquals(testPostDto.getTitle(), result.getTitle());
        assertEquals(testPostDto.getContent(), result.getContent());

        verify(postRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(postMapper, times(1)).convertPostToPostDto(testPost);
    }

    @Test
    void getById_PostNotFound_ThrowsException() {
        when(postRepository.findByIdAndDeletedFalse(123456)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> postServiceImpl.getById(123456));
        assertEquals(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(123456), exception.getMessage());

        verify(postRepository, times(1)).findByIdAndDeletedFalse(123456);
        verify(postMapper, never()).convertPostToPostDto(any(Post.class));
    }

    @Test
    void createPost_Ok() {
        NewPostRequest request = new NewPostRequest("new title", "new content");
        when(postRepository.existsByTitle(request.getTitle())).thenReturn(false);
        when((userRepository.findByIdAndDeletedFalse(1))).thenReturn(Optional.of(testUser));
        when(postMapper.convertNewPostDtoToPost(request)).thenReturn(testPost);
        when(postRepository.save(any())).thenReturn(testPost);
        when(postMapper.convertPostToPostDto(testPost)).thenReturn(testPostDto);

        PostDto result = postServiceImpl.save(request, 1).getBody();

        assertNotNull(result);
        assertEquals(testPostDto.getId(), result.getId());
        assertEquals(testPostDto.getTitle(), result.getTitle());
        assertEquals(testPostDto.getContent(), result.getContent());

        verify(postRepository, times(1)).existsByTitle(request.getTitle());
        verify(userRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(postMapper, times(1)).convertNewPostDtoToPost(request);
        verify(postRepository, times(1)).save(any());
        verify(postMapper, times(1)).convertPostToPostDto(testPost);
    }

    @Test
    void createPost_TitleAlreadyExists_DataExistsException() {
        NewPostRequest request = new NewPostRequest("new title", "new content");
        when(postRepository.existsByTitle(request.getTitle())).thenReturn(true);
        DataExistsException exception = assertThrows(DataExistsException.class, () -> postServiceImpl.save(request, 1));
        assertEquals(ApiErrorMessage.POST_WITH_THIS_TITLE_ALREADY_EXISTS.getMessage(request), exception.getMessage());

        verify(postRepository, times(1)).existsByTitle(request.getTitle());
        verify(userRepository, never()).findByIdAndDeletedFalse(1);
        verify(postMapper, never()).convertNewPostDtoToPost(request);
        verify(postRepository, never()).save(any());
        verify(postMapper, never()).convertPostToPostDto(testPost);
    }

    @Test
    void createPost_UserNotFound_NotFoundException() {
        NewPostRequest request = new NewPostRequest("new title", "new content");
        when(postRepository.existsByTitle(request.getTitle())).thenReturn(false);
        when(userRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> postServiceImpl.save(request, 1));
        assertEquals(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(1), exception.getMessage());

        verify(postRepository, times(1)).existsByTitle(request.getTitle());
        verify(userRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(postMapper, never()).convertNewPostDtoToPost(request);
        verify(postRepository, never()).save(any());
        verify(postMapper, never()).convertPostToPostDto(testPost);
    }

    @Test
    void updatedPost_Ok() {
        UpdatePostRequest request = new UpdatePostRequest("new title", "new content");
        when(postRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testPost));
        doNothing().when(accessValidator).validateAdminOrOwnerAccess(testPost.getUser().getId(), testUser.getId());
        when(postRepository.save(testPost)).thenReturn(testPost);
        when(postMapper.convertPostToPostDto(testPost)).thenReturn(testPostDto);

        PostDto result = postServiceImpl.updated(request, 1, testUser.getId()).getBody();

        assertNotNull(result);

        assertEquals(testPostDto.getId(), result.getId());
        assertEquals(testPostDto.getTitle(), result.getTitle());
        assertEquals(testPostDto.getContent(), result.getContent());

        verify(postRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(accessValidator, times(1)).validateAdminOrOwnerAccess(testPost.getUser().getId(), testUser.getId());
        verify(postRepository, times(1)).save(testPost);
        verify(postMapper, times(1)).convertPostToPostDto(testPost);
    }

    @Test
    void updatedPost_NotFount_NotFountException() {
        UpdatePostRequest request = new UpdatePostRequest("new title", "new content");
        when(postRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                postServiceImpl.updated(request, 1, testUser.getId()));
        assertEquals(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(1), exception.getMessage());

        verify(postRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(accessValidator, never()).validateAdminOrOwnerAccess(testPost.getUser().getId(), testUser.getId());
        verify(postRepository, never()).save(testPost);
        verify(postMapper, never()).convertPostToPostDto(testPost);
    }

    @Test
    void UpdatedPost_validateAdminOrOwnerAccess_AccessDeniedException() {
        UpdatePostRequest request = new UpdatePostRequest("new title", "new content");
        when(postRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testPost));
        doThrow(new AccessDeniedException(ApiErrorMessage.DONT_HAVE_ACCESS.getMessage(1)))
                .when(accessValidator)
                .validateAdminOrOwnerAccess(testPost.getUser().getId(), testUser.getId());

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                postServiceImpl.updated(request, 1, testUser.getId()));
        assertEquals(ApiErrorMessage.DONT_HAVE_ACCESS.getMessage(1), exception.getMessage());

        verify(postRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(accessValidator, times(1)).validateAdminOrOwnerAccess(testPost.getUser().getId(), testUser.getId());
        verify(postRepository, never()).save(testPost);
        verify(postMapper, never()).convertPostToPostDto(testPost);
    }

    @Test
    void deletedPost_Ok() {
        when(postRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testPost));
        doNothing().when(accessValidator).validateAdminOrOwnerAccess(testPost.getUser().getId(), testUser.getId());
        when(postRepository.save(testPost)).thenReturn(testPost);

        postServiceImpl.deleted(1, testUser.getId());

        verify(postRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(accessValidator, times(1)).validateAdminOrOwnerAccess(testPost.getUser().getId(), testUser.getId());
        verify(postRepository, times(1)).save(testPost);
    }

    @Test
    void deleted_NotFound_NotFoundException() {
        when(postRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                postServiceImpl.deleted(1, testUser.getId()));

        assertEquals(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(1), exception.getMessage());

        verify(postRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(accessValidator, never()).validateAdminOrOwnerAccess(testPost.getUser().getId(), testUser.getId());
        verify(postRepository, never()).save(testPost);
    }

    @Test
    void deleted_ValidateAdminOrOwnerAccess_AccessDeniedException() {
        when(postRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(testPost));
        doThrow(new AccessDeniedException(ApiErrorMessage.DONT_HAVE_ACCESS.getMessage()))
                .when(accessValidator).validateAdminOrOwnerAccess(testPost.getUser().getId(), testUser.getId());

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                postServiceImpl.deleted(1, testUser.getId()));

        assertEquals(ApiErrorMessage.DONT_HAVE_ACCESS.getMessage(1), exception.getMessage());

        verify(postRepository, times(1)).findByIdAndDeletedFalse(1);
        verify(accessValidator, times(1)).validateAdminOrOwnerAccess(testPost.getUser().getId(), testUser.getId());
        verify(postRepository, never()).save(testPost);
    }
}

