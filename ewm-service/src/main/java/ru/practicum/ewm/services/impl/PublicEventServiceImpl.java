package ru.practicum.ewm.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.constant.SortValue;
import ru.practicum.ewm.dtos.EventFullDto;
import ru.practicum.ewm.dtos.EventShortDto;
import ru.practicum.ewm.exception.ValidEntityException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.models.Event;
import ru.practicum.ewm.services.CommonEventService;
import ru.practicum.ewm.services.PublicEventService;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PublicEventServiceImpl implements PublicEventService {
    private final EntityManager entityManager;
    private final CommonEventService commonEventService;
    private final EventMapper eventMapper;

    public PublicEventServiceImpl(EntityManager entityManager, CommonEventService commonEventService,
                                  EventMapper eventMapper) {
        this.entityManager = entityManager;
        this.commonEventService = commonEventService;
        this.eventMapper = eventMapper;
    }

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Boolean onlyAvailable, SortValue sort,
                                         Integer from, Integer size, HttpServletRequest request) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = builder.createQuery(Event.class);

        Root<Event> root = query.from(Event.class);
        Predicate criteria = builder.conjunction();

        if (rangeStart != null && rangeEnd != null) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new ValidEntityException("rangeEnd must be after rangeStart");
            }
        }

        if (text != null) {
            Predicate annotationContain = builder.like(builder.lower(root.get("annotation")),
                    "%" + text.toLowerCase() + "%");
            Predicate descriptionContain = builder.like(builder.lower(root.get("description")),
                    "%" + text.toLowerCase() + "%");
            Predicate containText = builder.or(annotationContain, descriptionContain);

            criteria = builder.and(criteria, containText);
        }
        if (categories != null && categories.size() > 0) {
            Predicate containStates = root.get("category").in(categories);
            criteria = builder.and(criteria, containStates);
        }
        if (paid != null) {
            Predicate isPaid;
            if (paid) {
                isPaid = builder.isTrue(root.get("paid"));
            } else {
                isPaid = builder.isFalse(root.get("paid"));
            }
            criteria = builder.and(criteria, isPaid);
        }
        if (rangeStart != null) {
            Predicate greaterTime = builder.greaterThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), rangeStart);
            criteria = builder.and(criteria, greaterTime);
        }
        if (rangeEnd != null) {
            Predicate lessTime = builder.lessThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), rangeEnd);
            criteria = builder.and(criteria, lessTime);
        }

        query.select(root).where(criteria).orderBy(builder.asc(root.get("eventDate")));
        List<Event> events = entityManager.createQuery(query)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();

        if (onlyAvailable) {
            events = events.stream()
                    .filter((event -> event.getConfirmedRequests() < (long) event.getParticipantLimit()))
                    .collect(Collectors.toList());
        }

        if (sort != null) {
            if (sort == SortValue.EVENT_DATE) {
                events = events.stream().sorted(Comparator.comparing(Event::getEventDate)).collect(Collectors.toList());
            } else {
                events = events.stream().sorted(Comparator.comparing(Event::getViews)).collect(Collectors.toList());
            }
        }

        if (events.size() == 0) {
            return new ArrayList<>();
        }

        commonEventService.setViewAndConfirmedRequestsForEvents(events);
        commonEventService.sendStat(events, request);
        log.debug("Get event`s list with parameters");
        return eventMapper.mapToListEventShortDto(events);
    }

    @Override
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = commonEventService.getEventOrThrowException(eventId);
        commonEventService.setViewAndConfirmedRequestRequestsForTheEvent(event);
        commonEventService.sendStat(event, request);
        log.debug("Get event with ID = {} from public service", event);
        return eventMapper.mapToEventFullDto(event);
    }
}
