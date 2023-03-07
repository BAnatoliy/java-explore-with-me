package ru.practicum.ewm.services;

import ru.practicum.ewm.models.User;

public interface UserService {
    //List<EventShortDto> getEvents(Long userId, Integer from, Integer size);

    /*EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventsById(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    ParticipationRequestDto getEventRequest(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequest(Long userId, Long eventId,
                                                 EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<ParticipationRequestDto> getUserRequests(Long userId);

    ParticipationRequestDto createUserRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelUserRequest(Long userId, Long requestId);*/

    User getUserOrThrowException(Long userId);
}
