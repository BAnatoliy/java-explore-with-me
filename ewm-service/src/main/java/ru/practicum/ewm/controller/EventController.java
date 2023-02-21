package ru.practicum.ewm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.HitRequestDto;
import ru.practicum.ewm.client.EventClient;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/events/1")
public class EventController { // тестовый класс для проверки клиента
    private final EventClient eventClient;

    public EventController(EventClient eventClient) {
        this.eventClient = eventClient;
    }

    @PostMapping()
    public ResponseEntity<Object> post(HttpServletRequest request) {
        HitRequestDto hitDto = new HitRequestDto();
        hitDto.setUri(request.getRequestURI());
        hitDto.setIp(request.getRemoteAddr());
        hitDto.setApp("ewm-service");
        hitDto.setTimestamp(LocalDateTime.now());
        return eventClient.sendHit(hitDto);
    }

    @GetMapping()
    public ResponseEntity<Object> get(HttpServletRequest request) {
        Map<String, Object> parameters = Map.of(
                "start", "2021-09-06%2011%3A00%3A23",
                "end", "2023-09-06%2011%3A00%3A23",
                "uris", List.of("/events/1")
        );
        return eventClient.getStats(parameters);
    }
}
