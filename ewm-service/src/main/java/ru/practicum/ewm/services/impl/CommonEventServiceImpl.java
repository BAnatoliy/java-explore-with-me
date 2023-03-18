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

    /**
     * This method get the event`s data from the database by the list of IDs.
     * @param eventsIds list of events IDs to finding events data
     * @return list of {@link ru.practicum.ewm.models.Event events}
     */
    @Override
    public List<Event> getEventsByIds(List<Long> eventsIds) {
        List<Event> events = eventRepository.findAllById(eventsIds);
        log.debug("Get list events");
        return events;
    }

    /**
     * This method counts and sets amount views and requests with status "CONFIRMED" to list of events
     * @param events list of {@link ru.practicum.ewm.models.Event events} for setting count views and confirmed requests
     */
    @Override
    public void setViewsAndRequestsToEvents(List<Event> events) {
        LocalDateTime start = events.get(0).getCreatedOn(); //получение времени создания первого в списке события
        List<String> uris = new ArrayList<>(); //создания списка uri для направление запроса в сервис статистики
        /*создание map с ключем uri и значением события для сопоставления ответа из сервиса
        статистики с количеством просмотров uri и передачи этого количества конкретному событию*/
        Map<String, Event> eventsUri = new HashMap<>();
        String uri = "";
        for (Event event : events) {
            if (start.isBefore(event.getCreatedOn())) { //поиск самого раннего времени создания события из списка
                start = event.getCreatedOn();
            }
            uri = "/events/" + event.getId(); //создание значения uri с учетом ID события
            uris.add(uri); //добавление в список значения uri
            eventsUri.put(uri, event); //добавление в map значений uri и события
            event.setViews(0L); //количество просмотром события задается равное 0
        }

        //для обращение в сервис статистики для начала и конца периода поиска задается формат времени
        String startTime = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        //для значения времени конца задается значение настоящего времени
        String endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        //получаем из сервиса статистики список StatsDto, которое содержит значение uri и количество обращений к нему
        List<StatsDto> stats = getStats(startTime, endTime, uris);
        //из map получаем событие по ключу uri и данному событию задаем количество просмотров
        stats.forEach((stat) ->
                eventsUri.get(stat.getUri()) //из map получаем событие по ключу uri
                        .setViews(stat.getHits())); //событию задается количество обращений к uri полученное из StatsDto,

        //получаем список ID событий из переданного в метод списка
        List<Long> eventsId = events.stream().map(Event::getId).collect(Collectors.toList());
        //поиск подтвержденных запросов для переданных в метод событий
        List<ParticipationRequest> eventsRequests =
                participationRequestRepository.findAllByEvent_IdInAndStatusIs(eventsId, RequestStatus.CONFIRMED);
        //создание map, где ключ это ID события, а значение это количество подтвержденных запросов этого события
        Map<Long, Long> countConfirmedRequestEvents = eventsRequests.stream()
                .collect(Collectors.groupingBy((participationRequest ->
                        participationRequest.getEvent().getId()), Collectors.counting()));

        /*из map получаем по ключу (ID события) количество запросов, если для события запросов нет (null)
        задаем cобытию колчество запросов (confirmedRequests) равное 0, если в map есть значение для данного ключа,
        событию задается колчество запросов равное этому значению */
        events.forEach(event ->
                event.setConfirmedRequests(Objects.requireNonNullElse(countConfirmedRequestEvents.get(event.getId()), 0L)));
    }

    /**
     * This method counts and sets amount views and requests with status "CONFIRMED" to the event
     * @param event {@link ru.practicum.ewm.models.Event the Event} for setting count views and confirmed requests
     */
    @Override
    public void setViewsAndRequestsToEvent(Event event) {
        /*получаем время начала и конца поиска количество просмотров в сервере статистики
        время начала - время создания события, время конца - настоящее время*/
        String startTime = event.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        /*создаем список из одного значения uri с ID собатия,
        количество просмотров которого необходимо получить в сервисе статистики*/
        List<String> uris = List.of("/events/" + event.getId());

        //из сервиса статистики получает список StatsDto с количеством обращений для указанного события
        List<StatsDto> stats = getStats(startTime, endTime, uris);
        //если список из одного значения то передаем количество просмотров событию равное количеству обращений к uri
        if (stats.size() == 1) {
            event.setViews(stats.get(0).getHits());
        } else { //если список не имеет значения, количество просмотров задается равное 0
            event.setViews(0L);
        }

        //создается список для хранения запросов к указанному событию со подтвержденным статусом
        List<ParticipationRequest> eventsRequests;
        eventsRequests = participationRequestRepository.findAllByEvent_IdIsAndStatusIs(event.getId(),
                RequestStatus.CONFIRMED);

        //событию задается количество подтвержденных запросов равное размеру списка
        event.setConfirmedRequests((long) eventsRequests.size());
    }

    /**
     * This method gets the event data from the database by ID.
     * @param eventId ID of event which will be gotten
     * @return {@link ru.practicum.ewm.models.Event Event}
     */
    @Override
    public Event findEventById(Long eventId) {
        //поиск события по ID, если не найдено - исключение
        return eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Event with id=%s was not found", eventId))
        );
    }

    /**
     * This method send request to Stat Service with information about events.
     * @param events the information of which will be sent
     * @param request {@link javax.servlet.http.HttpServletRequest HttpServletRequest}
     */
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
        sendStatToEveryEvent(events, remoteAddr, now, nameService);
    }

    /**
     * This method send request to Stat Service with information about the event.
     * @param event the information of which will be sent
     * @param request {@link javax.servlet.http.HttpServletRequest HttpServletRequest}
     */
    @Override
    public void sendStat(Event event, HttpServletRequest request) {
        LocalDateTime now = LocalDateTime.now();
        String remoteAddr = request.getRemoteAddr();
        String nameService = "ewm-main-service";

        HitRequestDto requestDto = new HitRequestDto();
        requestDto.setTimestamp(now);
        requestDto.setUri("/events");
        requestDto.setApp(nameService);
        requestDto.setIp(request.getRemoteAddr());
        statServiceClient.sendHit(requestDto);
        sendStatToEvent(event.getId(), remoteAddr, now, nameService);
    }

    /**
     * This method send request to Stat Service with information about every event from the list.
     * @param events the information of which will be sent
     * @param remoteAddr IP address from which the request was sent
     * @param now the time of request sending
     * @param nameService the service`s name from which the request was sent
     */
    private void sendStatToEveryEvent(List<Event> events, String remoteAddr,
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

    /**
     * This method send request to Stat Service with information about the event.
     * @param eventId ID of event the information of which will be sent
     * @param remoteAddr IP address from which the request was sent
     * @param now the time of request sending
     * @param nameService the service`s name from which the request was sent
     */
    private void sendStatToEvent(Long eventId, String remoteAddr,
                                 LocalDateTime now, String nameService) {
        HitRequestDto requestDto = new HitRequestDto();
        requestDto.setTimestamp(now);
        requestDto.setUri("/events/" + eventId);
        requestDto.setApp(nameService);
        requestDto.setIp(remoteAddr);
        statServiceClient.sendHit(requestDto);
    }

    /**
     * This method send request to Stat Service to getting the information about uri visit statistics.
     * This method converts {@link Objects Objects} to {@link List List}
     * @param startTime the time to the start searching for information
     * @param endTime the time to the end searching for information
     * @param uris the time of request sending
     * @return list of {@link StatsDto} with the information about uri visit statistics.
     */
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
