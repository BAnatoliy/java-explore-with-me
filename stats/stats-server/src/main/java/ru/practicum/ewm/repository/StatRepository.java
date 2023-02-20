package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.StatsDto;
import ru.practicum.ewm.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<Hit, Long> {
    /*@Query("select h from Hit as h where (h.timestamp between ?1 and ?2) and (h.uri in ?3) group by h.ip")
    List<Hit> findSortByStartAndEndTimestampAndUriInGroupByIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    List<Hit> findByTimestampAfterAndTimestampBeforeAndUriIn(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select h from Hit as h where (h.timestamp between ?1 and ?2) group by h.ip")
    List<Hit> findSortByStartAndEndTimestampGroupByIp(LocalDateTime start, LocalDateTime end);

    List<Hit> findByTimestampAfterAndTimestampBefore(LocalDateTime start, LocalDateTime end);*/

    @Query(nativeQuery = true, name = "getStatsDtoGroupByIp")
    List<StatsDto> getStatsGroupByIp(@Param(value = "start") LocalDateTime start,
                                     @Param(value = "end") LocalDateTime end,
                                     @Param(value = "uris") List<String> uris);

    @Query(nativeQuery = true, name = "getStatsDto")
    List<StatsDto> getStats(@Param(value = "start") LocalDateTime start,
                            @Param(value = "end") LocalDateTime end,
                            @Param(value = "uris") List<String> uris);
}
