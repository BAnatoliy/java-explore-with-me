package ru.practicum.ewm.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dtos.CompilationDto;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.models.Compilation;
import ru.practicum.ewm.models.Event;
import ru.practicum.ewm.repositories.CompilationRepository;
import ru.practicum.ewm.services.CommonEventService;
import ru.practicum.ewm.services.CompilationPublicService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CompilationPublicServiceImpl implements CompilationPublicService {
    private final CompilationRepository compilationRepository;
    private final CommonEventService commonEventService;
    private final CompilationMapper compilationMapper;

    public CompilationPublicServiceImpl(CompilationRepository compilationRepository,
                                        CommonEventService commonEventService, CompilationMapper compilationMapper) {
        this.compilationRepository = compilationRepository;
        this.commonEventService = commonEventService;
        this.compilationMapper = compilationMapper;
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.findAllCompilation(pinned, from, size);
        } else {
            compilations = compilationRepository.findAllCompilation(from, size);
        }

        Set<Event> events = compilations.stream() //получаем уникальные event
                .flatMap(compilation -> compilation.getEvents().stream())
                .collect(Collectors.toSet());

        if (!events.isEmpty()) {
            commonEventService.setViewAndConfirmedRequestsForEvents(new ArrayList<>(events));
        }

        log.debug("Get list of compilations with parameter pinned {}", pinned);
        return compilationMapper.mapToListCompilationDto(compilations);
    }

    @Override
    public CompilationDto getCompilationsById(Long compId) {
        Compilation compilation = getCompilationOrThrowException(compId);
        Set<Event> setEvents = compilation.getEvents();
        if (!setEvents.isEmpty()) {
            commonEventService.setViewAndConfirmedRequestsForEvents(new ArrayList<>(setEvents));
        }
        log.debug("Get compilation with ID = {}", compId);
        return compilationMapper.mapToCompilationDto(compilation);
    }

    private Compilation getCompilationOrThrowException(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(() -> {
            log.debug("Compilation with ID {} not found", compId);
            return new EntityNotFoundException(String.format("Compilation with id=%s was not found", compId));
        });
    }
}
