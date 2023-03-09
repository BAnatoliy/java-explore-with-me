package ru.practicum.ewm.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.constant.EventState;
import ru.practicum.ewm.constant.StateActionForAdmin;
import ru.practicum.ewm.dtos.CategoryDto;
import ru.practicum.ewm.dtos.EventFullDto;
import ru.practicum.ewm.dtos.UpdateEventAdminRequest;
import ru.practicum.ewm.exception.ValidEntityException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.models.Event;
import ru.practicum.ewm.repositories.EventRepository;
import ru.practicum.ewm.services.AdminEventService;
import ru.practicum.ewm.services.CategoryService;
import ru.practicum.ewm.services.CommonEventService;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AdminEventServiceImpl implements AdminEventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final CategoryMapper categoryMapper;
    private final CommonEventService commonEventService;
    private final CategoryService categoryService;
    private final EntityManager entityManager;

    public AdminEventServiceImpl(EventRepository eventRepository, EventMapper eventMapper, CategoryMapper categoryMapper,
                                 CommonEventService commonEventService, CategoryService categoryService,
                                 EntityManager entityManager) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.categoryMapper = categoryMapper;
        this.commonEventService = commonEventService;
        this.categoryService = categoryService;
        this.entityManager = entityManager;
    }

    @Override
    public List<EventFullDto> getEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = builder.createQuery(Event.class);

        Root<Event> root = query.from(Event.class);
        Predicate criteria = builder.conjunction();

        if (rangeStart != null && rangeEnd != null) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new ValidEntityException("rangeEnd must be after rangeStart");
            }
        }

        if (users != null && users.size() > 0) {
            Predicate containUsersId = root.get("initiator").in(users);
            criteria = builder.and(criteria, containUsersId);
        }
        if (states != null && states.size() > 0) {
            Predicate containStates = root.get("state").in(states);
            criteria = builder.and(criteria, containStates);
        }
        if (categories != null && categories.size() > 0) {
            Predicate containStates = root.get("category").in(categories);
            criteria = builder.and(criteria, containStates);
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
        if (events.size() == 0) {
            return new ArrayList<>();
        }
        commonEventService.setViewAndConfirmedRequestsForEvents(events);
        log.debug("Get event`s list with parameters");
        return eventMapper.mapToListEventFullDto(events);
    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event oldEvent = commonEventService.getEventOrThrowException(eventId);
        validUpdateEvent(updateEventAdminRequest, oldEvent);

        if (updateEventAdminRequest.getAnnotation() != null) {
            oldEvent.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            CategoryDto categoryDto = categoryService.getCategoryById(updateEventAdminRequest.getCategory());
            oldEvent.setCategory(categoryMapper.mapToCategoryFromCategoryDto(categoryDto));
        }
        if (updateEventAdminRequest.getDescription() != null) {
            oldEvent.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            oldEvent.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            oldEvent.setLocation(updateEventAdminRequest.getLocation());
        }
        if (updateEventAdminRequest.getPaid() != null) {
            oldEvent.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            oldEvent.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getTitle() != null) {
            oldEvent.setTitle(updateEventAdminRequest.getTitle());
        }
        if (updateEventAdminRequest.getStateAction() == StateActionForAdmin.PUBLISH_EVENT) {
            oldEvent.setState(EventState.PUBLISHED);
            oldEvent.setPublishedOn(LocalDateTime.now());
            log.debug("Event with ID = {} is published", eventId);
        } else {
            oldEvent.setState(EventState.CANCELED);
            log.debug("Event with ID = {} is canceled", eventId);
        }
        Event savedEvent = eventRepository.save(oldEvent);
        commonEventService.setViewAndConfirmedRequestRequestsForTheEvent(savedEvent);
        return eventMapper.mapToEventFullDto(savedEvent);
    }

    private void validUpdateEvent(UpdateEventAdminRequest updateEventAdminRequest, Event event) {
        LocalDateTime eventDate = updateEventAdminRequest.getEventDate();
        if (eventDate != null && eventDate.isBefore(LocalDateTime.now().plusHours(1L))) {
            throw new ValidEntityException("Event`s time must be in one hour from now");
        }
        if (updateEventAdminRequest.getStateAction() == StateActionForAdmin.PUBLISH_EVENT &&
                event.getState() != EventState.PENDING) {
            throw new ValidEntityException("Event can be published only it has state pending");
        }
        if (updateEventAdminRequest.getStateAction() == StateActionForAdmin.REJECT_EVENT &&
                event.getState() == EventState.PUBLISHED) {
            throw new ValidEntityException("Event can be rejected only it hasn't state published");
        }
    }
}
