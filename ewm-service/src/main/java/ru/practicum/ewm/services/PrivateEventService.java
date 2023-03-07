package ru.practicum.ewm.services;

import ru.practicum.ewm.dtos.EventFullDto;
import ru.practicum.ewm.dtos.EventShortDto;
import ru.practicum.ewm.dtos.NewEventDto;
import ru.practicum.ewm.dtos.UpdateEventUserRequest;

import java.util.List;

public interface PrivateEventService {
    List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size);

    EventFullDto createEventByUser(Long userId, NewEventDto newEventDto);

    EventFullDto getEventsByIdByInitiator(Long userId, Long eventId);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);
}
