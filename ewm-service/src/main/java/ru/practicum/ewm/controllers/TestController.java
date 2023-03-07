/*
package ru.practicum.ewm.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.HitRequestDto;
import ru.practicum.ewm.HitResponseDto;
import ru.practicum.ewm.StatServiceClient;
import ru.practicum.ewm.StatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/events/1")
public class TestController { // тестовый класс для проверки клиента
    private final StatServiceClient statServiceClient;

    public TestController(StatServiceClient statServiceClient) {
        this.statServiceClient = statServiceClient;
    }

    @PostMapping()
    public ResponseEntity<Object> post(HttpServletRequest request) {
        HitRequestDto hitDto = new HitRequestDto();
        hitDto.setUri(request.getRequestURI());
        hitDto.setIp(request.getRemoteAddr());
        hitDto.setApp("ewm-service");
        hitDto.setTimestamp(LocalDateTime.now());
        return statServiceClient.sendHit(hitDto);
    }

    @GetMapping()
    public ResponseEntity<Object> get(HttpServletRequest request) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", "2021-09-06 11:00:23");
        parameters.put("end", "2023-09-06 11:00:23");
        parameters.put("uris", List.of("/events/1", "/events/2").toString().replace("[", "").replace("]", ""));
        parameters.put("unique", null);
                */
/*Map.of(
                "start", "2021-09-06%2011%3A00%3A23",
                "end", "2023-09-06%2011%3A00%3A23",
                "uris", List.of("/events/1")
        );*//*

        return statServiceClient.getStats(parameters);
    }
}
*/
