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
                                       CompilationRepository compilationRepository, CompilationMapper compilationMapper) {
        this.commonEventService = commonEventService;
        this.compilationRepository = compilationRepository;
        this.compilationMapper = compilationMapper;
    }

    @Override
    @Transactional
    public CompilationDto createCompilations(NewCompilationDto newCompilationDto) {
        List<Event> events = commonEventService.getEventsByIds(newCompilationDto.getEvents());
        Compilation compilation = new Compilation();
        compilation.setEvents(new HashSet<>(events));
        compilation.setPinned(newCompilationDto.getPinned());
        compilation.setTitle(newCompilationDto.getTitle());

        Compilation savedCompilation = compilationRepository.save(compilation);
        log.debug("Compilation is created");
        setViewAndRequestForEvents(savedCompilation);
        return compilationMapper.mapToCompilationDto(savedCompilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        getCompilationOrThrowException(compId);
        compilationRepository.deleteById(compId);
        log.debug("Compilation with ID = {} is deleted", compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation oldCompilation = getCompilationOrThrowException(compId);
        List<Long> eventsIds = updateCompilationRequest.getEvents();
        if (eventsIds != null) {
            List<Event> events = commonEventService.getEventsByIds(updateCompilationRequest.getEvents());
            oldCompilation.setEvents(new HashSet<>(events));
        }
        if (updateCompilationRequest.getPinned() != null) {
            oldCompilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            oldCompilation.setTitle(updateCompilationRequest.getTitle());
        }
        Compilation updatedCompilation = compilationRepository.save(oldCompilation);
        log.debug("Compilation with ID = {} is updated", compId);
        setViewAndRequestForEvents(updatedCompilation);
        return compilationMapper.mapToCompilationDto(updatedCompilation);
    }

    private Compilation getCompilationOrThrowException(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(() -> {
            log.debug("Compilation with ID {} not found", compId);
            return new EntityNotFoundException(String.format("Compilation with id=%s was not found", compId));
        });
    }

    private void setViewAndRequestForEvents(Compilation compilation) {
        Set<Event> setEvents = compilation.getEvents();
        if (!setEvents.isEmpty()) {
            List<Event> events = new ArrayList<>(setEvents);
            commonEventService.setViewAndConfirmedRequestsForEvents(events);
        }
    }
}
