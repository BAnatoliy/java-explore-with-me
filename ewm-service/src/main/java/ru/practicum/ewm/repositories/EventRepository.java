package ru.practicum.ewm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.models.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    /**
     * This method sends native query to getting a list of events from the table "events" by user`s ID
     * @param from amount of rows to skip
     * @param size amount of rows to getting
     * @param userId user`s ID who is an initiator events
     * @return a list of events or an empty list if events not found
     */
    @Query(value = "select * from events as e where e.user_id = :userId limit :size offset :from",
            nativeQuery = true)
    List<Event> findByUserIdByFromSize(@Param(value = "userId") Long userId,
                                       @Param(value = "from") Integer from,
                                       @Param(value = "size") Integer size);
}
