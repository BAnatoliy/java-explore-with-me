package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.StatsDto;
import ru.practicum.ewm.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<Hit, Long> {
    @Query(nativeQuery = true, name = "getStatsDtoWithUniqueIp")
    List<StatsDto> getStatsDtoWithUniqueIp(@Param(value = "start") LocalDateTime start,
                                     @Param(value = "end") LocalDateTime end,
                                     @Param(value = "uris") List<String> uris);

    @Query(nativeQuery = true, name = "getStatsDto")
    List<StatsDto> getStats(@Param(value = "start") LocalDateTime start,
                            @Param(value = "end") LocalDateTime end,
                            @Param(value = "uris") List<String> uris);
}
