package ru.practicum.ewm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.models.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    /**
     * This method sends query to getting a list of compilations from the table "compilations" by event`s ID
     * @param from amount of rows to skip
     * @param size amount of rows to getting
     * @param pinned pinning criteria on the main page
     * @return a list of compilations or an empty list if compilations not found
     */
    @Query(value = "select * from compilations as c where c.pinned = :pinned limit :size offset :from",
            nativeQuery = true)
    List<Compilation> findAllCompilation(@Param(value = "pinned") Boolean pinned,
                                         @Param(value = "from") Integer from,
                                         @Param(value = "size") Integer size);

    /**
     * This method sends native query to getting a list of all compilations from the table "compilations" by event`s ID
     * @param from amount of rows to skip
     * @param size amount of rows to getting
     * @return a list of compilations or an empty list if compilations not found
     */
    @Query(value = "select * from compilations as c where c.id > :from limit :size",
            nativeQuery = true)
    List<Compilation> findAllCompilation(@Param(value = "from") Integer from,
                                         @Param(value = "size") Integer size);
}
