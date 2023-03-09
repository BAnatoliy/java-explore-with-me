package ru.practicum.ewm.services;

import ru.practicum.ewm.constant.SortValue;
import ru.practicum.ewm.dtos.EventFullDto;
import ru.practicum.ewm.dtos.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface PublicEventService {
    List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd, Boolean onlyAvailable, SortValue sort,
                                  Integer from, Integer size, HttpServletRequest request);

    EventFullDto getEventById(Long eventId, HttpServletRequest request);
}
