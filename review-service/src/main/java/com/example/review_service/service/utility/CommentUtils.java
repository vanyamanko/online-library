package com.example.review_service.service.utility;

import com.example.review_service.dto.CommentDto;
import com.example.review_service.model.Comment;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CommentUtils {
    public static CommentDto.Response buildCommentDtoFromModel (Comment comment) {
        return CommentDto.Response.builder()
                .id(comment.getId())
                .reviewId(comment.getReview().getId())
                .userId(comment.getUserId())
                .text(comment.getText())
                .createdAt(comment.getCreatedAt())
                .build();
    }

}
