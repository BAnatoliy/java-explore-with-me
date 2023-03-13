package ru.practicum.ewm.services;

import ru.practicum.ewm.models.Event;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CommonEventService {
    void setViewsAndRequestsToEvents(List<Event> events);

    void setViewsAndRequestsToEvent(Event event);

    List<Event> getEventsByIds(List<Long> eventsIds);

    Event findEventById(Long eventId);

    void sendStat(List<Event> events, HttpServletRequest request);

    void sendStat(Event event, HttpServletRequest request);
}
