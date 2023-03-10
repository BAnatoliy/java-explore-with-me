package ru.practicum.ewm.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.constant.RequestStatus;
import ru.practicum.ewm.dtos.CommentDto;
import ru.practicum.ewm.dtos.NewCommentDto;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.exception.ValidEntityException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.models.Comment;
import ru.practicum.ewm.models.Event;
import ru.practicum.ewm.models.User;
import ru.practicum.ewm.repositories.CommentRepository;
import ru.practicum.ewm.repositories.ParticipationRequestRepository;
import ru.practicum.ewm.services.CommentService;
import ru.practicum.ewm.services.CommonEventService;
import ru.practicum.ewm.services.UserService;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final CommonEventService commonEventService;
    private final ParticipationRequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final EntityManager entityManager;

    public CommentServiceImpl(CommentRepository commentRepository, UserService userService,
                              CommonEventService commonEventService, ParticipationRequestRepository requestRepository,
                              EventMapper eventMapper, EntityManager entityManager) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.commonEventService = commonEventService;
        this.requestRepository = requestRepository;
        this.eventMapper = eventMapper;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public CommentDto updateComment(NewCommentDto newCommentDto, Long userId, Long commentId) {
        Comment oldComment = checkAuthorOfCommentAndReturnComment(userId, commentId);
        oldComment.setText(newCommentDto.getText());
        oldComment.setEdited(LocalDateTime.now());
        Comment savedComment = commentRepository.save(oldComment);
        log.debug("Comment with ID = {} was update", commentId);
        return eventMapper.mapToCommentDto(savedComment);
    }

    @Override
    @Transactional
    public CommentDto createComment(NewCommentDto newCommentDto, Long userId, Long eventId) {
        User author = userService.getUserOrThrowException(userId);
        Event event = commonEventService.getEventOrThrowException(eventId);
        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ValidEntityException(String.format("Event with ID = %s not finished yet", eventId));
        }
        requestRepository.findByRequester_IdAndEvent_IdAndStatusIs(userId, eventId, RequestStatus.CONFIRMED)
                .orElseThrow(() ->
                        new ValidEntityException(String.format("User with ID = %s wasn`t participant by event with ID = %s",
                                userId, eventId)));

        Comment comment = new Comment();
        comment.setText(newCommentDto.getText());
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(author);
        comment.setEvent(event);
        Comment savedComment = commentRepository.save(comment);
        log.debug("Comment was save");
        return eventMapper.mapToCommentDto(savedComment);
    }

    @Override
    public List<CommentDto> getUserCommentsByCreateTime(Long userId, LocalDateTime createStart,
                                                        LocalDateTime createEnd, Integer from, Integer size) {
        userService.getUserOrThrowException(userId);

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Comment> query = builder.createQuery(Comment.class);

        Root<Comment> root = query.from(Comment.class);

        if (createStart != null && createEnd != null) {
            if (createEnd.isBefore(createStart)) {
                throw new ValidEntityException("createEnd must be after createStart");
            }
        }

        Predicate criteria = root.get("author").in(userId);
        if (createStart != null) {
            Predicate greaterTime = builder.greaterThanOrEqualTo(
                    root.get("created").as(LocalDateTime.class), createStart);
            criteria = builder.and(criteria, greaterTime);
        }
        if (createEnd != null) {
            Predicate lessTime = builder.lessThanOrEqualTo(
                    root.get("created").as(LocalDateTime.class), createEnd);
            criteria = builder.and(criteria, lessTime);
        }
        query.select(root).where(criteria).orderBy(builder.asc(root.get("created")));
        List<Comment> comments = entityManager.createQuery(query)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();
        log.debug("Get comment`s list of user with ID = {}", userId);
        return eventMapper.mapToListCommentDto(comments);
    }

    @Override
    public List<CommentDto> getCommentsByEventIdByAdmin(Long eventId, Integer from, Integer size) {
        List<Comment> eventComments = commentRepository.findAllByEvent_Id(eventId, from, size);
        log.debug("Get comment`s list of event with ID = {}", eventId);
        return eventMapper.mapToListCommentDto(eventComments);
    }

    @Override
    public CommentDto getCommentsByIdByUser(Long userId, Long commentId) {
        Comment comment = checkAuthorOfCommentAndReturnComment(userId, commentId);
        log.debug("Get comment with ID = {}", commentId);
        return eventMapper.mapToCommentDto(comment);
    }

    @Override
    public CommentDto getCommentsByIdByAdmin(Long commentId) {
        Comment comment = getCommentOrThrowException(commentId);
        log.debug("Comment with ID = {} was found", commentId);
        return eventMapper.mapToCommentDto(comment);
    }

    @Override
    @Transactional
    public void deleteCommentByUser(Long userId, Long commentId) {
        checkAuthorOfCommentAndReturnComment(userId, commentId);
        deleteComment(commentId);
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        deleteComment(commentId);
    }

    private void deleteComment(Long commentId) {
        getCommentOrThrowException(commentId);
        commentRepository.deleteById(commentId);
        log.debug("Comment with ID = {} was delete", commentId);
    }

    private Comment getCommentOrThrowException(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Comment with id=%s was not found", commentId)));
    }

    private Comment checkAuthorOfCommentAndReturnComment(Long userId, Long commentId) {
        userService.getUserOrThrowException(userId);
        Comment comment = getCommentOrThrowException(commentId);
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ValidEntityException(String.format("User is not author comment with ID = %s", commentId));
        }
        return comment;
    }
}
