package ru.practicum.ewm.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.HitRequestDto;
import ru.practicum.ewm.HitResponseDto;
import ru.practicum.ewm.StatsDto;
import ru.practicum.ewm.service.StatService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping
public class StatController {
    private final StatService statService;

    public StatController(StatService statService) {
        this.statService = statService;
    }

    @PostMapping("/hit")
    public HitResponseDto createStat(@RequestBody HitRequestDto hitsDto) {
        return statService.createStat(hitsDto);
    }

    /*@GetMapping("/stats")
    public List<HitResponseDto> getStats(@RequestParam(value = "start") LocalDateTime start,
                                         @RequestParam(value = "end") LocalDateTime end,
                                         @RequestParam(value = "uris", required = false) List<String> uris,
                                         @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        return statService.getStats(start, end, uris, unique);
    }*/

    @GetMapping("/stats")
    public List<StatsDto> getStats(@RequestParam(value = "start") String start,
                                   @RequestParam(value = "end") String end,
                                   @RequestParam(value = "uris", required = false) List<String> uris,
                                   @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        return statService.getStats(start, end, uris, unique);
    }

    @GetMapping("/stats/{statId}")
    public HitResponseDto getStatById(@PathVariable(value = "statId") Long statId) {
        return statService.getStatById(statId);
    }
}
