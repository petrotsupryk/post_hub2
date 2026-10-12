package petro.post_hub2.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import petro.post_hub2.kafka.service.KafkaMessageService;
import petro.post_hub2.mapper.PostMapper;
import petro.post_hub2.model.constants.ApiErrorMessage;
import petro.post_hub2.model.dto.PostDto;
import petro.post_hub2.model.dto.PostSearchDto;
import petro.post_hub2.model.entity.Post;
import petro.post_hub2.model.entity.User;
import petro.post_hub2.model.exceptions.DataExistsException;
import petro.post_hub2.model.exceptions.NotFoundException;
import petro.post_hub2.model.request.NewPostRequest;
import petro.post_hub2.model.request.PostSearchRequest;
import petro.post_hub2.model.request.UpdatePostRequest;
import petro.post_hub2.model.response.CustomResponse;
import petro.post_hub2.model.response.PaginationResponse;
import petro.post_hub2.repository.PostRepository;
import petro.post_hub2.repository.UserRepository;
import petro.post_hub2.repository.criteria.PostSearchCriteria;
import petro.post_hub2.security.validation.AccessValidator;
import petro.post_hub2.service.PostService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final AccessValidator accessValidator;
    private final PostMapper postMapper;
    private final KafkaMessageService kafkaMessageService;

    @Override
    public CustomResponse<PostDto> getById(int id) {
        Post post = postRepository.findByIdAndDeletedFalse(id).orElseThrow(() ->
                new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(id)));
        PostDto postDto = postMapper.convertPostToPostDto(post);
        kafkaMessageService.sendPostGetLog(post.getId());
        return CustomResponse.success(postDto);
    }

    @Override
    public CustomResponse<PostDto> save(NewPostRequest newPostRequest, int userId) {
        if (postRepository.existsByTitle(newPostRequest.getTitle())) {
            throw new DataExistsException(ApiErrorMessage.POST_WITH_THIS_TITLE_ALREADY_EXISTS.getMessage(newPostRequest));
        }
        User user = userRepository.findByIdAndDeletedFalse(userId).orElseThrow(() ->
                new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_EMAIL.getMessage(userId)));
        Post post = postMapper.convertNewPostDtoToPost(newPostRequest);
        post.setUser(user);
        post = postRepository.save(post);
        PostDto postDto = postMapper.convertPostToPostDto(post);
        kafkaMessageService.sendPostCreateLog(post.getId(), userId);
        return CustomResponse.success(postDto);
    }

    @Override
    public CustomResponse<PostDto> updated(UpdatePostRequest updatePostRequest, int postId, int userId) {
        Post post = postRepository.findByIdAndDeletedFalse(postId).orElseThrow(() ->
                new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(postId)));
        accessValidator.validateAdminOrOwnerAccess(post.getUser().getId(), userId);
        post.setTitle(updatePostRequest.getTitle());
        post.setContent(updatePostRequest.getContent());
        post.setUpdated(LocalDateTime.now());
        post = postRepository.save(post);
        PostDto postDto = postMapper.convertPostToPostDto(post);
        kafkaMessageService.sendPostUpdateLog(postId, userId);
        return CustomResponse.success(postDto);
    }

    @Override
    public void deleted(int id, int userId) {
        Post post = postRepository.findByIdAndDeletedFalse(id).orElseThrow(() ->
                new NotFoundException(ApiErrorMessage.POST_NOT_FOUND_BY_ID.getMessage(id)));
        accessValidator.validateAdminOrOwnerAccess(post.getUser().getId(), userId);
        post.setDeleted(true);
        postRepository.save(post);
        kafkaMessageService.sendPostDeleLog(post.getId(), userId);
    }

    @Override
    public CustomResponse<PaginationResponse<PostSearchDto>> findAllPostsByPagination(Pageable pageable) {
        Page<PostSearchDto> posts = postRepository.findAll(pageable).map(postMapper::convertPostToPostSearchDto);
        PaginationResponse<PostSearchDto> paginationResponse = new PaginationResponse<>(
                posts.getContent(),
                new PaginationResponse.Pagination(
                        posts.getTotalElements(),
                        pageable.getPageSize(),
                        pageable.getPageNumber() + 1,
                        posts.getTotalPages()
                )
        );
        kafkaMessageService.sendPostGetAllByPagination();
        return CustomResponse.success(paginationResponse);
    }

    @Override
    public CustomResponse<PaginationResponse<PostSearchDto>> findAllPostsByPaginationAndParams(Pageable pageable, PostSearchRequest postSearchRequest) {
        Specification<Post> specification = new PostSearchCriteria(postSearchRequest);
        Page<PostSearchDto> posts = postRepository.findAll(specification, pageable).map(postMapper::convertPostToPostSearchDto);
        PaginationResponse<PostSearchDto> paginationResponse = new PaginationResponse<>(
                posts.getContent(),
                new PaginationResponse.Pagination(
                        posts.getTotalElements(),
                        pageable.getPageSize(),
                        pageable.getPageNumber() + 1,
                        posts.getTotalPages()
                )
        );
        kafkaMessageService.sendPostGetAllByPaginationAndParam();
        return CustomResponse.success(paginationResponse);
    }


}
