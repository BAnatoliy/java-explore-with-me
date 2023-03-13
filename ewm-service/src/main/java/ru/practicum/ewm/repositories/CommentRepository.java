package ru.practicum.ewm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.models.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(value = "select * from comments where event_id = :eventId order by created_time limit :size offset :from",
            nativeQuery = true)
    List<Comment> findAllByEvent_Id(@Param(value = "eventId") Long eventId, @Param(value = "from") Integer from,
                                    @Param(value = "size") Integer size);
}
