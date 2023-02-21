package ru.practicum.ewm.service;

import ru.practicum.ewm.HitRequestDto;
import ru.practicum.ewm.HitResponseDto;
import ru.practicum.ewm.StatsDto;

import java.util.List;

public interface StatService {
    HitResponseDto createStat(HitRequestDto statsDto);

    List<StatsDto> getStats(String start, String end, List<String> uris, boolean unique);
}
