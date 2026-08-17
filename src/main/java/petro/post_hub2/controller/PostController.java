package petro.post_hub2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import petro.post_hub2.model.dto.PostDto;
import petro.post_hub2.model.dto.PostSearchDto;
import petro.post_hub2.model.request.NewPostRequest;
import petro.post_hub2.model.request.PostSearchRequest;
import petro.post_hub2.model.request.UpdatePostRequest;
import petro.post_hub2.model.response.CustomResponse;
import petro.post_hub2.model.response.PaginationResponse;
import petro.post_hub2.security.CustomUserDetails;
import petro.post_hub2.service.PostService;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/post")
@Tag(name = "post", description = "post methods")
public class PostController {
    private final PostService postService;

    @GetMapping("/{id}")
    @Operation(summary = "get post by id",
            description = "get and return post by id")
    @ApiResponses(value =
    @ApiResponse(
            responseCode = "200",
            description = "successful get post by id",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "message": "success",
                              "body": {
                                "id": 2,
                                "title": "roman info",
                                "content": "get info",
                                "created": "2026-08-10T22:30:21.685831",
                                "updated": "2026-08-10T22:30:21.685831"
                              },
                              "success": true
                            }
                            """))
    ))
    public ResponseEntity<CustomResponse<PostDto>> getById(@PathVariable int id) {
        CustomResponse<PostDto> response = postService.getById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    @Operation(summary = "save post",
            description = "save and return post")
    @ApiResponses(value =
    @ApiResponse(responseCode = "200",
            description = "successful saved post",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "message": "success",
                              "body": {
                                "id": 3,
                                "title": "roman_mentor",
                                "content": "success",
                                "created": "2026-08-10T22:33:09.9181202",
                                "updated": "2026-08-10T22:33:09.9181202"
                              },
                              "success": true
                            }
                            """))))
    public ResponseEntity<CustomResponse<PostDto>> save(@Validated NewPostRequest newPostRequest,
                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        CustomResponse<PostDto> response = postService.save(newPostRequest, userDetails.getUser().getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "updated post",
            description = "updated and return post")
    @ApiResponses(value =
    @ApiResponse(
            responseCode = "200",
            description = "successful updated post",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "message": "success",
                              "body": {
                                "id": 2,
                                "title": "roman info",
                                "content": "lesson",
                                "created": "2026-08-10T22:30:21.685831",
                                "updated": "2026-08-10T22:51:26.3866074"
                              },
                              "success": true
                            }
                            """))
    ))
    public ResponseEntity<CustomResponse<PostDto>> updated(@Validated UpdatePostRequest updatePostRequest,
                                                           @AuthenticationPrincipal CustomUserDetails userDetails,
                                                           @PathVariable int id) {
        CustomResponse<PostDto> response = postService.updated(updatePostRequest, id, userDetails.getUser().getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "deleted post",
            description = "deleted and mark post as deleted")
    public ResponseEntity<Void> deleted(@PathVariable int id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.deleted(id, userDetails.getUser().getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    @Operation(summary = "get all",
            description = "get all with pagination")
    @ApiResponses(value =
    @ApiResponse(
            responseCode = "200",
            description = "successful get all posts",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "message": "success",
                              "body": {
                                "content": [
                                  {
                                    "id": 3,
                                    "title": "roman_mentor",
                                    "content": "success",
                                    "created": "2026-08-10T22:33:09.91812",
                                    "updated": "2026-08-10T22:33:09.91812",
                                    "deleted": false
                                  },
                                  {
                                    "id": 1,
                                    "title": "roman1",
                                    "content": "lesson",
                                    "created": "2026-08-10T22:29:40.543222",
                                    "updated": "2026-08-10T22:35:43.571111",
                                    "deleted": true
                                  }
                                ],
                                "pagination": {
                                  "total": 3,
                                  "limit": 2,
                                  "page": 1,
                                  "pages": 2
                                }
                              },
                              "success": true
                            }
                            """))
    ))
    public ResponseEntity<CustomResponse<PaginationResponse<PostSearchDto>>> findPostAllByPagination(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "10") int limit
    ) {
        Pageable pageable = PageRequest.of(page, limit);
        CustomResponse<PaginationResponse<PostSearchDto>> response = postService.findAllPostsByPagination(pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    @Operation(summary = "get search",
            description = "get all with pagination and param")
    @ApiResponses(value =
    @ApiResponse(
            responseCode = "200",
            description = "get all post with pagination and param",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            
                            """))
    ))
    public ResponseEntity<CustomResponse<PaginationResponse<PostSearchDto>>> findPostAllByPaginationWithParam(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "10") int limit,
            @RequestBody @Valid PostSearchRequest postSearchRequest
    ) {
        Pageable pageable = PageRequest.of(page, limit);
        CustomResponse<PaginationResponse<PostSearchDto>> response = postService.findAllPostsByPaginationAndParams(pageable, postSearchRequest);
        return ResponseEntity.ok(response);
    }

}
