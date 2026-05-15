package com.example.postclientservice.dto.container;



import com.example.postclientservice.dto.Dto.CommentaryDto;

import java.util.List;



public class CommentaryContainerDto {
    private final List<CommentaryDto> commentaries;

    public CommentaryContainerDto(List<CommentaryDto> commentaries) {
        this.commentaries = commentaries;
    }

    public List<CommentaryDto> getCommentaries() {
        return commentaries;
    }

}
