package ru.practicum.ewm.services;

import ru.practicum.ewm.constant.EventState;
import ru.practicum.ewm.dtos.EventFullDto;
import ru.practicum.ewm.dtos.UpdateEventAdminRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventService {
    List<EventFullDto> getEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                 LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}
