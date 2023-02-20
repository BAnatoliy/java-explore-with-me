package ru.practicum.ewm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.HitRequestDto;
import ru.practicum.ewm.HitResponseDto;
import ru.practicum.ewm.HitMapper;
import ru.practicum.ewm.StatsDto;
import ru.practicum.ewm.exception.StateNotFoundException;
import ru.practicum.ewm.model.Hit;
import ru.practicum.ewm.repository.StatRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class StatServiceImpl implements StatService{
    private final HitMapper hitMapper;
    private final StatRepository statRepository;

    public StatServiceImpl(HitMapper hitMapper, StatRepository statRepository) {
        this.hitMapper = hitMapper;
        this.statRepository = statRepository;
    }

    @Override
    public HitResponseDto createStat(HitRequestDto hitDto) {
        Hit hit = hitMapper.mapToHit(hitDto);
        /*String ip = request.getRemoteAddr();
        try {
            stat.setIp(InetAddress.getByName(ip));
        } catch (UnknownHostException e) {
            throw new WrongIpException("Ip is wrong");
        }
        stat.setUri(request.getRequestURI());*/
        Hit savedHit = statRepository.save(hit);
        log.debug("Stat saved");
        return hitMapper.mapToDto(savedHit);
    }

    @Override
    public List<StatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        List<StatsDto> stats;
        LocalDateTime startTime = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endTime = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        /*String decodeStart = URLDecoder.decode(start, StandardCharsets.UTF_8);
        LocalDateTime startTime = LocalDateTime.parse(decodeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String decodeEnd = URLDecoder.decode(end, StandardCharsets.UTF_8);
        LocalDateTime endTime = LocalDateTime.parse(decodeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));*/

        if (!unique) {
            stats = statRepository.getStats(startTime, endTime, uris);
            log.debug("Get stats when unique is false");
        } else {
            stats = statRepository.getStatsGroupByIp(startTime, endTime, uris);
            log.debug("Get stats when unique is true");
        }
        return stats;
    }

    /*@Override
    public List<HitResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<Hit> hits;
        *//*String decodeStart = URLDecoder.decode(start, StandardCharsets.UTF_8);
        LocalDateTime startTime = LocalDateTime.parse(decodeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String decodeEnd = URLDecoder.decode(end, StandardCharsets.UTF_8);
        LocalDateTime endTime = LocalDateTime.parse(decodeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));*//*

        if (uris == null && !unique) {
            hits = statRepository.findByTimestampAfterAndTimestampBefore(start, end);
            log.debug("Get stats when uris is null and unique is false");
        } else if (uris != null && !unique) {
            hits = statRepository.findByTimestampAfterAndTimestampBeforeAndUriIn(start, end, uris);
            log.debug("Get stats when uris isn`t null and unique is false");
        } else if (uris != null) {
            hits = statRepository.findSortByStartAndEndTimestampAndUriInGroupByIp(start, end, uris);
            log.debug("Get stats when uris isn`t null and unique is true");
        } else {
            hits = statRepository.findSortByStartAndEndTimestampGroupByIp(start, end);
            log.debug("Get stats when uris is null and unique is true");
        }
        return hitMapper.mapToListDto(hits);
    }*/

    @Override
    public HitResponseDto getStatById(Long statId) {
        Hit hit = statRepository.findById(statId).orElseThrow(() -> new StateNotFoundException("Stat not found"));
        log.debug("Stat with ID = {} is found", statId);
        return hitMapper.mapToDto(hit);
    }
}
