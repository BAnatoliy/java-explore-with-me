package ru.practicum.ewm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.models.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = "select * from events as e where e.user_id = :userId limit :size offset :from",
            nativeQuery = true)
    List<Event> findByUserIdByFromSize(@Param(value = "userId") Long userId,
                                       @Param(value = "from") Integer from,
                                       @Param(value = "size") Integer size);
}
