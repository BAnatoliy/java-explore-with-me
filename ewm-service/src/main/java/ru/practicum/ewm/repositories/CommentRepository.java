package ru.practicum.ewm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.models.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    /**
     * This method sends native query to getting a list of comments from the table "comments" by event`s ID
     * @param from amount of rows to skip
     * @param size amount rows to getting
     * @param eventId event`s ID witch the comments belong
     * @return a list of comments or an empty list if comments not found
     */
    @Query(value = "select * from comments where event_id = :eventId order by created_time limit :size offset :from",
            nativeQuery = true)
    List<Comment> findAllByEvent_Id(@Param(value = "eventId") Long eventId, @Param(value = "from") Integer from,
                                    @Param(value = "size") Integer size);
}
