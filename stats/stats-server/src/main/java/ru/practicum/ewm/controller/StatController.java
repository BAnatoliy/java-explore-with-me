package ru.practicum.ewm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.HitRequestDto;
import ru.practicum.ewm.HitResponseDto;
import ru.practicum.ewm.StatsDto;
import ru.practicum.ewm.service.StatService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping
@Validated
public class StatController {
    private final StatService statService;

    public StatController(StatService statService) {
        this.statService = statService;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitResponseDto createStat(@RequestBody @Valid HitRequestDto hitsDto) {
        return statService.createStat(hitsDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> getStats(@RequestParam(value = "start") String start,
                                   @RequestParam(value = "end") String end,
                                   @RequestParam(value = "uris", required = false) List<String> uris,
                                   @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        return statService.getStats(start, end, uris, unique);
    }
}
