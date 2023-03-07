package ru.practicum.ewm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.models.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query(value = "select * from events as e left join compilations_events as ce on e.id = ce.event_id " +
            "where ce.compilation_id in :comp_ids order by e.event_date",
            nativeQuery = true)
    List<Event> findAllByCompId(@Param(value = "comp_ids") List<Long> compId);

    @Query(value = "select * from events as e where user_id = :userId limit :size offset :from",
            nativeQuery = true)
    List<Event> findAllByInitiatorByFromSize(@Param(value = "userId") Long userId,
                                             @Param(value = "from") Integer from,
                                             @Param(value = "size") Integer size);

    @Query(value = "select * from events as e where e.user_id = :userId limit :size offset :from",
            nativeQuery = true)
    List<Event> findByUserIdByFromSize(@Param(value = "userId") Long userId,
                                       @Param(value = "from") Integer from,
                                       @Param(value = "size") Integer size);
}
