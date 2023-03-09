package ru.practicum.ewm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.models.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query(value = "select * from compilations as c where c.pinned = :pinned limit :size offset :from",
            nativeQuery = true)
    List<Compilation> findAllCompilation(@Param(value = "pinned") Boolean pinned,
                                         @Param(value = "from") Integer from,
                                         @Param(value = "size") Integer size);

    @Query(value = "select * from compilations as c where c.id > :from limit :size",
            nativeQuery = true)
    List<Compilation> findAllCompilation(@Param(value = "from") Integer from,
                                         @Param(value = "size") Integer size);
}
