package ru.practicum.ewm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.HitMapper;
import ru.practicum.ewm.HitRequestDto;
import ru.practicum.ewm.HitResponseDto;
import ru.practicum.ewm.StatsDto;
import ru.practicum.ewm.exception.ValidationParametersException;
import ru.practicum.ewm.model.Hit;
import ru.practicum.ewm.repository.StatRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class StatServiceImpl implements StatService {
    private final HitMapper hitMapper;
    private final StatRepository statRepository;

    public StatServiceImpl(HitMapper hitMapper, StatRepository statRepository) {
        this.hitMapper = hitMapper;
        this.statRepository = statRepository;
    }

    @Override
    public HitResponseDto createStat(HitRequestDto hitDto) {
        Hit hit = hitMapper.mapToHit(hitDto);
        Hit savedHit = statRepository.save(hit);
        log.debug("Stat saved");
        return hitMapper.mapToDto(savedHit);
    }

    @Override
    public List<StatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        List<StatsDto> stats;
        LocalDateTime startTime = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endTime = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        validParameters(startTime, endTime, uris);

        if (!unique) {
            stats = statRepository.getStats(startTime, endTime, uris);
            log.debug("Get stats when unique is false");
        } else {
            stats = statRepository.getStatsDtoWithUniqueIp(startTime, endTime, uris);
            log.debug("Get stats when unique is true");
        }
        return stats;
    }

    private void validParameters(LocalDateTime startTime, LocalDateTime endTime, List<String> uris) {
        if (startTime.isAfter(endTime)) {
            log.debug("Parameters is not valid");
            throw new ValidationParametersException("Start time don`t be later end time");
        }
        if (uris == null || uris.isEmpty()) {
            log.debug("Parameters is not valid");
            throw new ValidationParametersException("List uris don`t be empty and null");
        }
    }
}
