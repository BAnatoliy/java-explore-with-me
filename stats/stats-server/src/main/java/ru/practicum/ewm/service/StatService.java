package ru.practicum.ewm.service;

import ru.practicum.ewm.HitRequestDto;
import ru.practicum.ewm.HitResponseDto;
import ru.practicum.ewm.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    HitResponseDto createStat(HitRequestDto statsDto);

    //List<HitResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
    List<StatsDto> getStats(String start, String end, List<String> uris, boolean unique);

    HitResponseDto getStatById(Long statId);
}
