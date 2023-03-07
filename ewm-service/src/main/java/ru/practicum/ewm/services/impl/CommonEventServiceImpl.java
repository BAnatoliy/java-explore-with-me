package ru.practicum.ewm.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.HitRequestDto;
import ru.practicum.ewm.StatServiceClient;
import ru.practicum.ewm.StatsDto;
import ru.practicum.ewm.constant.RequestStatus;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.models.Event;
import ru.practicum.ewm.models.ParticipationRequest;
import ru.practicum.ewm.repositories.EventRepository;
import ru.practicum.ewm.repositories.ParticipationRequestRepository;
import ru.practicum.ewm.services.CommonEventService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommonEventServiceImpl implements CommonEventService {
    private final StatServiceClient statServiceClient;
    private final ParticipationRequestRepository participationRequestRepository;
    private final EventRepository eventRepository;

    public CommonEventServiceImpl(StatServiceClient statServiceClient,
                                  ParticipationRequestRepository participationRequestRepository,
                                  EventRepository eventRepository) {
        this.statServiceClient = statServiceClient;
        this.participationRequestRepository = participationRequestRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public List<Event> getEventsByIds(List<Long> eventsIds) {
        List<Event> events = eventRepository.findAllById(eventsIds);
        log.debug("Get list events");
        return events;
    }

    @Override
    public void setViewAndConfirmedRequestsForEvents(List<Event> events) {
        LocalDateTime start = events.get(0).getCreatedOn();
        List<String> uris = new ArrayList<>();
        Map<String, Event> eventsUri = new HashMap<>();
        String uri = "";
        for (Event event : events) {
            if (start.isBefore(event.getCreatedOn())) {
                start = event.getCreatedOn();
            }
            uri = "/events/" + event.getId();
            uris.add(uri);
            eventsUri.put(uri, event);
            event.setViews(0L);
        }

        String startTime = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<StatsDto> stats = getStats(startTime, endTime, uris);
        stats.forEach((stat) ->
                eventsUri.get(stat.getUri()).setViews(stat.getHits()));

        List<Long> eventsId = events.stream().map(Event::getId).collect(Collectors.toList());
        List<ParticipationRequest> eventsRequests = participationRequestRepository.findAllByEvent_IdInAndStatusIs(eventsId, RequestStatus.CONFIRMED);
        Map<Long, Long> countConfirmedRequestEvents = eventsRequests.stream()
                .collect(Collectors.groupingBy((participationRequest ->
                        participationRequest.getEvent().getId()), Collectors.counting()));

        events.forEach(event ->
                event.setConfirmedRequests(Objects.requireNonNullElse(countConfirmedRequestEvents.get(event.getId()), 0L)));
    }

    @Override
    public void setViewAndConfirmedRequestRequestsForTheEvent(Event event) {
        String startTime = event.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<String> uris = List.of("/events/" + event.getId());

        List<StatsDto> stats = getStats(startTime, endTime, uris);
        if (stats.size() == 1) {
            event.setViews(stats.get(0).getHits());
        } else {
            event.setViews(0L);
        }

        List<ParticipationRequest> eventsRequests;
        eventsRequests = participationRequestRepository.findAllByEvent_IdIsAndStatusIs(event.getId(), RequestStatus.CONFIRMED);
        event.setConfirmedRequests((long) eventsRequests.size());
    }

    @Override
    public Event getEventOrThrowException(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Event with id=%s was not found", eventId))
        );
    }

    @Override
    public void sendStat(List<Event> events, HttpServletRequest request) {
        LocalDateTime now = LocalDateTime.now();
        String remoteAddr = request.getRemoteAddr();
        String nameService = "ewm-main-service";

        HitRequestDto requestDto = new HitRequestDto();
        requestDto.setTimestamp(now);
        requestDto.setUri("/events");
        requestDto.setApp(nameService);
        requestDto.setIp(request.getRemoteAddr());
        statServiceClient.sendHit(requestDto);
        sendStatForEveryEvent(events, remoteAddr, now, nameService);
    }

    @Override
    public void sendStat(Event events, HttpServletRequest request) {
        LocalDateTime now = LocalDateTime.now();
        String remoteAddr = request.getRemoteAddr();
        String nameService = "ewm-main-service";

        HitRequestDto requestDto = new HitRequestDto();
        requestDto.setTimestamp(now);
        requestDto.setUri("/events");
        requestDto.setApp(nameService);
        requestDto.setIp(request.getRemoteAddr());
        statServiceClient.sendHit(requestDto);
        sendStatForTheEvent(events.getId(), remoteAddr, now, nameService);
    }

    private void sendStatForEveryEvent(List<Event> events, String remoteAddr,
                                       LocalDateTime now, String nameService) {
        for (Event event : events) {
            HitRequestDto requestDto = new HitRequestDto();
            requestDto.setTimestamp(now);
            requestDto.setUri("/events/" + event.getId());
            requestDto.setApp(nameService);
            requestDto.setIp(remoteAddr);
            statServiceClient.sendHit(requestDto);
        }
    }

    private void sendStatForTheEvent(Long eventId, String remoteAddr,
                                     LocalDateTime now, String nameService) {
        HitRequestDto requestDto = new HitRequestDto();
        requestDto.setTimestamp(now);
        requestDto.setUri("/events/" + eventId);
        requestDto.setApp(nameService);
        requestDto.setIp(remoteAddr);
        statServiceClient.sendHit(requestDto);
    }

    private List<StatsDto> getStats(String startTime, String endTime, List<String> uris) {
        ResponseEntity<Object> response = statServiceClient.getStats(startTime, endTime, uris, false);
        List<StatsDto> stats;
        ObjectMapper mapper = new ObjectMapper();
        try {
            stats = Arrays.asList(mapper.readValue(mapper.writeValueAsString(response.getBody()), StatsDto[].class));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return stats;
    }
}
