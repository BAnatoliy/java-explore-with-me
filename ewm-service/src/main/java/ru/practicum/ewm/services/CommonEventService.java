package ru.practicum.ewm.services;

import ru.practicum.ewm.models.Event;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CommonEventService {
    void setViewAndConfirmedRequestsForEvents(List<Event> events);

    void setViewAndConfirmedRequestRequestsForTheEvent(Event event);

    List<Event> getEventsByIds(List<Long> eventsIds);

    Event getEventOrThrowException(Long eventId);

    void sendStat(List<Event> events, HttpServletRequest request);

    void sendStat(Event event, HttpServletRequest request);
}
