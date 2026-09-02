package petro.post_hub2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import petro.post_hub2.model.dto.CommentDto;
import petro.post_hub2.model.dto.CommentSearchDto;
import petro.post_hub2.model.request.CommentSearchRequest;
import petro.post_hub2.model.request.NewCommentRequest;
import petro.post_hub2.model.request.UpdateCommentRequest;
import petro.post_hub2.model.response.CustomResponse;
import petro.post_hub2.model.response.PaginationResponse;
import petro.post_hub2.security.CustomUserDetails;
import petro.post_hub2.service.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
@Validated
@Tag(name = "comment",
        description = "methods comments")
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/{id}")
    @Operation(summary = "get by id",
            description = "get comment by id")
    @ApiResponses(value =
    @ApiResponse(
            responseCode = "200",
            description = "successful get comment by id",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "message": "success",
                              "body": {
                                "id": 1,
                                "message": "comment to post 7",
                                "ownerId": 5,
                                "postId": 7,
                                "created": "2026-09-02T21:22:22.931952",
                                "updated": "2026-09-02T21:22:22.931952"
                              },
                              "success": true
                            }
                            """))
    ))
    public ResponseEntity<CustomResponse<CommentDto>> getById(@PathVariable int id) {
        CustomResponse<CommentDto> response = commentService.getCommentById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    @Operation(summary = "create comment",
            description = "get save comment")
    @ApiResponses(value =
    @ApiResponse(
            responseCode = "200",
            description = "successful created comment",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                                                        {
                              "message": "success",
                              "body": {
                                "id": 1,
                                "message": "comment to post 7",
                                "ownerId": 5,
                                "postId": 7,
                                "created": "2026-09-02T21:22:22.9319521",
                                "updated": "2026-09-02T21:22:22.9319521"
                              },
                              "success": true
                            }
                            """))
    ))
    public ResponseEntity<CustomResponse<CommentDto>> save(@Valid @RequestBody NewCommentRequest newCommentRequest,
                                                           @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        CustomResponse<CommentDto> response = commentService.createComment(newCommentRequest, customUserDetails.getUser().getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "update comment",
            description = "get updated comment"
    )
    @ApiResponses(value =
    @ApiResponse(
            responseCode = "200",
            description = "successful updated comment",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "message": "success",
                              "body": {
                                "id": 1,
                                "message": "updated comment 7",
                                "ownerId": 5,
                                "postId": 7,
                                "created": "2026-09-02T21:22:22.931952",
                                "updated": "2026-09-02T21:22:22.931952"
                              },
                              "success": true
                            }
                            """))
    ))
    public ResponseEntity<CustomResponse<CommentDto>> updated(@PathVariable int id,
                                                              @Valid @RequestBody UpdateCommentRequest updateCommentRequest,
                                                              @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        CustomResponse<CommentDto> response = commentService.updateComment(id, updateCommentRequest, customUserDetails.getUser().getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete comment",
            description = "get comment and get mark as deleted")
    public ResponseEntity<Void> delete(@PathVariable int id,
                                       @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        commentService.delete(id, customUserDetails.getUser().getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    @Operation(summary = "get all comment",
            description = "get all comment with pagination")
    @ApiResponses(value =
    @ApiResponse(
            responseCode = "200",
            description = "successful get all comments",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "message": "success",
                              "body": {
                                "content": [
                                  {
                                    "id": 2,
                                    "userId": 5,
                                    "postId": 8,
                                    "created": "2026-09-02T21:39:58.827806",
                                    "updated": "2026-09-02T21:39:58.827806",
                                    "deleted": false
                                  },
                                  {
                                    "id": 1,
                                    "userId": 5,
                                    "postId": 7,
                                    "created": "2026-09-02T21:22:22.931952",
                                    "updated": "2026-09-02T21:22:22.931952",
                                    "deleted": false
                                  }
                                ],
                                "pagination": {
                                  "total": 2,
                                  "limit": 10,
                                  "page": 1,
                                  "pages": 1
                                }
                              },
                              "success": true
                            }
                            """))
    ))
    public ResponseEntity<CustomResponse<PaginationResponse<CommentSearchDto>>> findAllCommentWithPagination(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "10") int limit
    ) {
        Pageable pageable = PageRequest.of(page, limit);
        CustomResponse<PaginationResponse<CommentSearchDto>> response = commentService.findAllCommentWithPagination(pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    @Operation(summary = "get search by comment",
            description = "get all comment with pagination and param")
    @ApiResponses(value =
    @ApiResponse(
            responseCode = "200",
            description = "successful search with pagination and param",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "message": "success",
                              "body": {
                                "content": [
                                  {
                                    "id": 2,
                                    "userId": 5,
                                    "postId": 8,
                                    "created": "2026-09-02T21:39:58.827806",
                                    "updated": "2026-09-02T21:39:58.827806",
                                    "deleted": false
                                  }
                                ],
                                "pagination": {
                                  "total": 1,
                                  "limit": 10,
                                  "page": 1,
                                  "pages": 1
                                }
                              },
                              "success": true
                            }
                            """))
    ))
    public ResponseEntity<CustomResponse<PaginationResponse<CommentSearchDto>>> findAllCommentWithPaginationAndParam(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "10") int limit,
            @RequestBody @Valid CommentSearchRequest commentSearchRequest
    ) {
        Pageable pageable = PageRequest.of(page, limit);
        CustomResponse<PaginationResponse<CommentSearchDto>> response = commentService.findAllCommentWithPaginationAndParams(
                commentSearchRequest, pageable
        );
        return ResponseEntity.ok(response);
    }
}
