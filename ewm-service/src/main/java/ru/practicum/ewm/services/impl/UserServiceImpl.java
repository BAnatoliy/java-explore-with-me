package ru.practicum.ewm.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.mapper.MapperDto;
import ru.practicum.ewm.models.User;
import ru.practicum.ewm.repositories.EventRepository;
import ru.practicum.ewm.repositories.UserRepository;
import ru.practicum.ewm.services.CommonEventService;
import ru.practicum.ewm.services.UserService;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final MapperDto mapperDto;
    private final CommonEventService commonEventService;

    public UserServiceImpl(EventRepository eventRepository, UserRepository userRepository,
                           MapperDto mapperDto, CommonEventService commonEventService) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.mapperDto = mapperDto;
        this.commonEventService = commonEventService;
    }

    @Override
    public User getUserOrThrowException(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("User with id=%s was not found", userId))
        );
    }
/*
    @Override
    public List<EventShortDto> getEvents(Long userId, Integer from, Integer size) {
        getUserOrThrowException(userId);
        List<Event> events = eventRepository.findAllByInitiatorByFromSize(userId, from, size);
        log.debug("Get list events with initiator ID = {} and parameters from = {}, size = {}", userId, from, size);
        return mapperDto.mapToListEventShortDto(events);
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        getUserOrThrowException(userId);

        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new ValidationFieldDtoException("The time of the event cannot be less than " +
                    "two hours from the present time");
        }

        Event event = mapperDto.mapToEvent(newEventDto);
        Event savedEvent = eventRepository.save(event);
        log.debug("New event is saved");
        return mapperDto.mapToEventFullDto(savedEvent);
    }

    @Override
    public EventFullDto getEventsById(Long userId, Long eventId) {
        getUserOrThrowException(userId);
        Event event = commonEventService.getEventOrThrowException(eventId);
        log.debug("Event with ID = {} is found", event);
        return mapperDto.mapToEventFullDto(event);
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        getUserOrThrowException(userId);
        Event oldEvent = commonEventService.getEventOrThrowException(eventId);

        if (oldEvent.getState().equals(EventState.PUBLISHED) &&
                updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new ValidationFieldDtoException("Status of event can be only PENDING or CANCELED and The time of " +
                    "the event cannot be less than two hours from the present time");
        }

        if (updateEventUserRequest.getAnnotation() != null) {
            oldEvent.setAnnotation(updateEventUserRequest.getAnnotation());
            log.debug("Annotation updated");
        }
        if (updateEventUserRequest.getCategory() != null) {
            Category category = new Category();
            category.setId(updateEventUserRequest.getCategory());
            oldEvent.setCategory(category);
            log.debug("Category updated");
        }
        if (updateEventUserRequest.getDescription() != null) {

        }
        if (updateEventUserRequest.getEventDate() != null) {

        }
        if (updateEventUserRequest.getLocation() != null) {

        }
        if (updateEventUserRequest.getPaid() != null) {

        }
        if (updateEventUserRequest.getParticipantLimit() != null) {

        }
        if (updateEventUserRequest.getRequestModeration() != null) {

        }
        if (updateEventUserRequest.getStateAction() != null) {

        }
        if (updateEventUserRequest.getTitle() != null) {

        }
        return null;
    }*/
}
