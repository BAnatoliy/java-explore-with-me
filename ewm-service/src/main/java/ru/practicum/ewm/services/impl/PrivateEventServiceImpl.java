package ru.practicum.ewm.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.constant.EventState;
import ru.practicum.ewm.constant.StateActionForUser;
import ru.practicum.ewm.dtos.*;
import ru.practicum.ewm.exception.ValidEntityException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.models.Category;
import ru.practicum.ewm.models.Event;
import ru.practicum.ewm.models.User;
import ru.practicum.ewm.repositories.EventRepository;
import ru.practicum.ewm.services.CommonEventService;
import ru.practicum.ewm.services.PrivateEventService;
import ru.practicum.ewm.services.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PrivateEventServiceImpl implements PrivateEventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final CommonEventService commonEventService;
    private final CategoryServiceImpl categoryService;
    private final EventMapper eventMapper;
    private final CategoryMapper categoryMapper;

    public PrivateEventServiceImpl(EventRepository eventRepository, UserService userService,
                                   CommonEventService commonEventService, CategoryServiceImpl categoryService,
                                   EventMapper eventMapper, CategoryMapper categoryMapper) {
        this.eventRepository = eventRepository;
        this.userService = userService;
        this.commonEventService = commonEventService;
        this.categoryService = categoryService;
        this.eventMapper = eventMapper;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional
    public EventFullDto createEventByUser(Long userId, NewEventDto newEventDto) {
        User initiator = userService.getUserOrThrowException(userId);
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidEntityException("Event must be two hour after now");
        }
        Event event = eventMapper.mapToEvent(newEventDto);

        CategoryDto categoryDto = categoryService.getCategoryById(newEventDto.getCategory());

        event.setCategory(categoryMapper.mapToCategoryFromCategoryDto(categoryDto));
        event.setInitiator(initiator);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);

        Event savedEvent = eventRepository.save(event);
        savedEvent.setViews(0L);
        savedEvent.setConfirmedRequests(0L);
        log.debug("Event was created");
        return eventMapper.mapToEventFullDto(savedEvent);
    }

    @Override
    public List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size) {
        userService.getUserOrThrowException(userId);
        List<Event> events = eventRepository.findByUserIdByFromSize(userId, from, size);
        if (events.size() == 0) {
            return new ArrayList<>();
        }
        commonEventService.setViewAndConfirmedRequestsForEvents(events);
        log.debug("Get events list by the user with ID = {}", userId);
        return eventMapper.mapToListEventShortDto(events);
    }

    @Override
    public EventFullDto getEventsByIdByInitiator(Long userId, Long eventId) {
        userService.getUserOrThrowException(userId);
        Event event = getEvent(userId, eventId);
        commonEventService.setViewAndConfirmedRequestRequestsForTheEvent(event);
        log.debug("Get event with ID = {} and initiator with ID = {}", eventId, userId);
        return eventMapper.mapToEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        if (updateEventUserRequest.getEventDate() != null &&
                updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidEntityException("Event must be two hour after now");
        }
        userService.getUserOrThrowException(userId);
        Event oldEvent = getEvent(userId, eventId);
        if (oldEvent.getState() == EventState.PUBLISHED) {
            throw new ValidEntityException("Event must be updated if state is not published");
        }

        if (updateEventUserRequest.getAnnotation() != null) {
            oldEvent.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            Category category = categoryMapper.mapToCategoryFromCategoryDto(
                    categoryService.getCategoryById(updateEventUserRequest.getCategory()));
            oldEvent.setCategory(category);
        }
        if (updateEventUserRequest.getDescription() != null) {
            oldEvent.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            oldEvent.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.getLocation() != null) {
            oldEvent.setLocation(updateEventUserRequest.getLocation());
        }
        if (updateEventUserRequest.getPaid() != null) {
            oldEvent.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            oldEvent.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getStateAction() == StateActionForUser.SEND_TO_REVIEW) {
            oldEvent.setState(EventState.PENDING);
        } else {
            oldEvent.setState(EventState.CANCELED);
        }
        if (updateEventUserRequest.getTitle() != null) {
            oldEvent.setTitle(updateEventUserRequest.getTitle());
        }
        Event updatedEvent = eventRepository.save(oldEvent);
        commonEventService.setViewAndConfirmedRequestRequestsForTheEvent(updatedEvent);
        log.debug("Event with ID = {} is updated", eventId);
        return eventMapper.mapToEventFullDto(updatedEvent);
    }

    private Event getEvent(Long userId, Long eventId) {
        Event event = commonEventService.getEventOrThrowException(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidEntityException(String.format("User with ID = %s is not initiator the event", userId));
        }
        return event;
    }
}
