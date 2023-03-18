package ru.practicum.ewm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.models.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * This method sends query to getting a list of participation request
     * from the table "requests" by events and status
     * @param ids IDs of users to getting
     * @param from amount of rows to skip
     * @param size amount of rows to getting
     * @return a list of users or an empty list if users not found
     */
    @Query(value = "select * from users as u where u.id in :ids limit :size offset :from", nativeQuery = true)
    List<User> getUserByParameters(@Param(value = "ids") List<Long> ids, @Param(value = "from") Integer from,
                                   @Param(value = "size") Integer size);

}
