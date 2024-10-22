package com.project.shopapp.controllers;

import com.project.shopapp.dtos.CommentDTO;
import com.project.shopapp.models.User;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.responses.comment.CommentResponse;
import com.project.shopapp.services.comment.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("${api.prefix}/comments")
//@Validated
//Dependency Injection
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("")
    public ResponseEntity<List<CommentResponse>> getAllComments(
            @RequestParam(value = "user_id", required = false) Long userId,
            @RequestParam("product_id") Long productId
    ) {
        List<CommentResponse> commentResponses;
        if (userId == null) {
            commentResponses = commentService.getCommentsByProduct(productId);
        } else {
            commentResponses = commentService.getCommentsByUserAndProduct(userId, productId);
        }
        return ResponseEntity.ok(commentResponses);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> updateComment(
            @PathVariable("id") Long commentId,
            @Valid @RequestBody CommentDTO commentDTO
    ) throws Exception {
        User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!Objects.equals(loginUser.getId(), commentDTO.getUserId())) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("You cannot update another user's comment")
                    .status(HttpStatus.BAD_REQUEST)
                    .build());

        }
        commentService.updateComment(commentId, commentDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Update comment successfully")
                .status(HttpStatus.OK)
                .build());
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> insertComment(
            @Valid @RequestBody CommentDTO commentDTO
    ) {
        // Insert the new comment
        User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (loginUser.getId() != commentDTO.getUserId()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message("You cannot comment as another user")
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
        commentService.insertComment(commentDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Insert comment successfully")
                .status(HttpStatus.OK)
                .build());
    }
}
