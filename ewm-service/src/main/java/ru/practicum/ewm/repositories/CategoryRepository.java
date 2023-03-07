package ru.practicum.ewm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.models.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = "select * from categories as c where c.id > :from limit :size", nativeQuery = true)
    List<Category> findAllByFromSize(@Param(value = "from") Integer from, @Param(value = "size") Integer size);
}
