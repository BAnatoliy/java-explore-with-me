package ru.practicum.ewm.services;

import ru.practicum.ewm.dtos.CommentDto;
import ru.practicum.ewm.dtos.NewCommentDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {
    List<CommentDto> getCommentsByEventIdByAdmin(Long eventId, Integer from, Integer size);

    CommentDto getCommentsByIdByAdmin(Long commentId);

    void deleteCommentByAdmin(Long commentId);

    void deleteCommentByUser(Long userId, Long commentId);

    List<CommentDto> getUserCommentsByCreateTime(Long userId, LocalDateTime createStart,
                                                 LocalDateTime createEnd, Integer from, Integer size);

    CommentDto getCommentsByIdByUser(Long userId, Long commentId);

    CommentDto updateComment(NewCommentDto newCommentDto, Long userId, Long eventId);

    CommentDto createComment(NewCommentDto newCommentDto, Long userId, Long eventId);
}
