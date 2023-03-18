package ru.practicum.ewm.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dtos.CompilationDto;
import ru.practicum.ewm.dtos.NewCompilationDto;
import ru.practicum.ewm.dtos.UpdateCompilationRequest;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.models.Compilation;
import ru.practicum.ewm.models.Event;
import ru.practicum.ewm.repositories.CompilationRepository;
import ru.practicum.ewm.services.AdminCompilationService;
import ru.practicum.ewm.services.CommonEventService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class AdminCompilationServiceImpl implements AdminCompilationService {
    private final CommonEventService commonEventService;
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    public AdminCompilationServiceImpl(CommonEventService commonEventService,
                                       CompilationRepository compilationRepository,
                                       CompilationMapper compilationMapper) {
        this.commonEventService = commonEventService;
        this.compilationRepository = compilationRepository;
        this.compilationMapper = compilationMapper;
    }

    /**
     * This method creates the compilation`s data obtained from the NewCompilationDto in the database
     * @param newCompilationDto {@link ru.practicum.ewm.dtos.NewCompilationDto dto} which the compilation is created from
     * @return {@link ru.practicum.ewm.dtos.CompilationDto CompilationDto} received from
     * {@link ru.practicum.ewm.models.Compilation Compilation}
     */
    @Override
    @Transactional
    public CompilationDto createCompilations(NewCompilationDto newCompilationDto) {
        //получение списка событий из БД по списку ID данных событий
        List<Event> events = commonEventService.getEventsByIds(newCompilationDto.getEvents());
        Compilation compilation = new Compilation();
        compilation.setEvents(new HashSet<>(events));
        compilation.setPinned(newCompilationDto.getPinned());
        compilation.setTitle(newCompilationDto.getTitle());

        Compilation savedCompilation = compilationRepository.save(compilation);
        log.debug("Compilation is created");
        // заполнение просмотров и запросов на участие для событий данной подборки
        setViewAndRequestToEvents(savedCompilation);
        return compilationMapper.mapToCompilationDto(savedCompilation);
    }

    /**
     * This method deletes the compilation by ID from the database
     * @param compId ID of compilation which will be deleted
     */
    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        findCompilationById(compId); // поиск подборки по ID, если не найдена - исключение
        compilationRepository.deleteById(compId);
        log.debug("Compilation with ID = {} is deleted", compId);
    }

    /**
     * This method updates the compilation`s data obtained from the UpdateCompilationRequest
     * in the database.
     * @param updateCompilationRequest {@link ru.practicum.ewm.dtos.UpdateCompilationRequest dto}
     * which the compilation is updated from
     * @param compId ID of compilation which will be updated
     * @return {@link ru.practicum.ewm.dtos.CompilationDto CompilationDto} gotten from
     * {@link ru.practicum.ewm.models.Compilation Compilation}
     */
    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation oldCompilation = findCompilationById(compId); //поиск подборки по ID, если не найдена - исключение
        List<Long> eventsIds = updateCompilationRequest.getEvents(); //получение списка ID событий из подборки для обновления
        if (eventsIds != null) { //если список ID событий не null, то из БД запрашивается список событий по данным ID
            List<Event> events = commonEventService.getEventsByIds(updateCompilationRequest.getEvents());
            oldCompilation.setEvents(new HashSet<>(events)); //обновляемой подборки задается множество событий
        }
        if (updateCompilationRequest.getPinned() != null) {
            oldCompilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            oldCompilation.setTitle(updateCompilationRequest.getTitle());
        }
        Compilation updatedCompilation = compilationRepository.save(oldCompilation);
        log.debug("Compilation with ID = {} is updated", compId);
        //заполнение просмотров и запросов на участие для событий данной подборки
        setViewAndRequestToEvents(updatedCompilation);
        return compilationMapper.mapToCompilationDto(updatedCompilation);
    }

    /**
     * This method get the compilation data from the database by ID.
     * @param compId ID of compilation which will be gotten
     * @return {@link ru.practicum.ewm.models.Compilation Compilation}
     */
    private Compilation findCompilationById(Long compId) {
        //поиск подборки по ID, если не найдена - исключение
        return compilationRepository.findById(compId).orElseThrow(() -> {
            log.debug("Compilation with ID {} not found", compId);
            return new EntityNotFoundException(String.format("Compilation with id=%s was not found", compId));
        });
    }

    /**
     * This method get the compilation data from the database by ID.
     * This method behaves as though it invokes
     * {@link ru.practicum.ewm.services.CommonEventService#setViewsAndRequestsToEvents(List)}
     * @param compilation which events are set
     */
    private void setViewAndRequestToEvents(Compilation compilation) {
        //получение множества событий из подборки
        Set<Event> setEvents = compilation.getEvents();
        //если множество не пустое, из множества создается список событий, у которых заполняются просмотры и запросы
        if (!setEvents.isEmpty()) {
            List<Event> events = new ArrayList<>(setEvents);
            commonEventService.setViewsAndRequestsToEvents(events);
        }
    }
}
