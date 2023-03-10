package ru.practicum.ewm.controllers.priv;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dtos.CommentDto;
import ru.practicum.ewm.dtos.NewCommentDto;
import ru.practicum.ewm.services.CommentService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/comments")
@Validated
public class PrivateCommentController {
    private final CommentService commentService;

    public PrivateCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{eventId}")
    public CommentDto createComment(@RequestBody @Valid NewCommentDto newCommentDto,
                                    @PathVariable(value = "userId") Long userId,
                                    @PathVariable(value = "eventId") Long eventId) {
        return commentService.createComment(newCommentDto, userId, eventId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@RequestBody @Valid NewCommentDto newCommentDto,
                                    @PathVariable(value = "userId") Long userId,
                                    @PathVariable(value = "commentId") Long commentId) {
        return commentService.updateComment(newCommentDto, userId, commentId);
    }

    @GetMapping("/{commentId}")
    public CommentDto getCommentById(@PathVariable(value = "userId") Long userId,
                                     @PathVariable(value = "commentId") Long commentId) {
        return commentService.getCommentsByIdByUser(userId, commentId);
    }

    @GetMapping
    public List<CommentDto> getUserCommentsByCreateTime(@PathVariable(value = "userId") Long userId,
                                                        @RequestParam(value = "from") Integer from,
                                                        @RequestParam(value = "size") Integer size,
                                                        @RequestParam(value = "createStart") LocalDateTime createStart,
                                                        @RequestParam(value = "createEnd") LocalDateTime createEnd) {
        return commentService.getUserCommentsByCreateTime(userId, createStart, createEnd, from, size);
    }

    @DeleteMapping("/{commentId}")
    public void deleteCommentByUser(@PathVariable(value = "userId") Long userId,
                                    @PathVariable(value = "commentId") Long commentId) {
        commentService.deleteCommentByUser(userId, commentId);
    }
}
