package petro.post_hub2.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import petro.post_hub2.mapper.PostMapper;
import petro.post_hub2.model.constants.ApiErrorMessage;
import petro.post_hub2.model.dto.PostDto;
import petro.post_hub2.model.dto.PostSearchDto;
import petro.post_hub2.model.entity.Post;
import petro.post_hub2.model.exceptions.NotFoundException;
import petro.post_hub2.model.request.NewPostRequest;
import petro.post_hub2.model.request.PostSearchRequest;
import petro.post_hub2.model.request.UpdatePostRequest;
import petro.post_hub2.model.response.CustomResponse;
import petro.post_hub2.model.response.PaginationResponse;
import petro.post_hub2.repository.PostRepository;
import petro.post_hub2.repository.UserRepository;
import petro.post_hub2.security.PostService;
import petro.post_hub2.security.validation.AccessValidator;

import java.awt.print.Pageable;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final AccessValidator accessValidator;
    private final PostMapper postMapper;

    @Override
    public CustomResponse<PostDto> getById(int id) {
        Post post = postRepository.findByIdAndDeletedFalse(id).orElseThrow(() ->
                new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(id)));
        PostDto postDto = postMapper.convertPostToPostDto(post);
        return CustomResponse.success(postDto);
    }

    @Override
    public CustomResponse<PostDto> save(NewPostRequest newPostRequest) {
        return null;
    }

    @Override
    public CustomResponse<PostDto> updated(UpdatePostRequest updatePostRequest, int postId, int id) {
        return null;
    }

    @Override
    public void deleted(int id) {
        Post post = postRepository.findByIdAndDeletedFalse(id).orElseThrow(() ->
                new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(id)));
        accessValidator.validateAdminOrOwnerAccess();
    }

    @Override
    public CustomResponse<PaginationResponse<PostSearchDto>> findAllPostByPagination(Pageable pageable) {
        return null;
    }

    @Override
    public CustomResponse<PaginationResponse<PostSearchDto>> findAllPostByPaginationAndParam(Pageable pageable, PostSearchRequest postSearchRequest) {
        return null;
    }
}
