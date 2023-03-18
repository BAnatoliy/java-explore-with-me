package ru.practicum.ewm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.models.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    /**
     * This method sends native query to getting a list of categories from the table "categories"
     * @param from amount of rows to skip
     * @param size amount of rows to getting
     * @return a list of categories or an empty list if categories not found
     */
    @Query(value = "select * from categories as c where c.id > :from limit :size", nativeQuery = true)
    List<Category> findAllByFromSize(@Param(value = "from") Integer from, @Param(value = "size") Integer size);
}
