package ru.practicum.ewm.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.StatServiceClient;
import ru.practicum.ewm.dtos.CompilationDto;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.mapper.MapperDto;
import ru.practicum.ewm.models.Compilation;
import ru.practicum.ewm.models.Event;
import ru.practicum.ewm.repositories.CompilationRepository;
import ru.practicum.ewm.repositories.EventRepository;
import ru.practicum.ewm.services.CommonEventService;
import ru.practicum.ewm.services.CompilationPublicService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CompilationPublicServiceImpl implements CompilationPublicService {
    private final CompilationRepository compilationRepository;
    private final CommonEventService commonEventService;
    private final EventRepository eventRepository;
    private final StatServiceClient statServiceClient;
    private final MapperDto mapperDto;

    public CompilationPublicServiceImpl(CompilationRepository compilationRepository, CommonEventService commonEventService,
                                        EventRepository eventRepository, StatServiceClient statServiceClient, MapperDto mapperDto) {
        this.compilationRepository = compilationRepository;
        this.commonEventService = commonEventService;
        this.eventRepository = eventRepository;
        this.statServiceClient = statServiceClient;
        this.mapperDto = mapperDto;
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
        return mapperDto.mapToListCompilationDto(compilations);
    }

    @Override
    public CompilationDto getCompilationsById(Long compId) {
        Compilation compilation = getCompilationOrThrowException(compId);
        Set<Event> setEvents = compilation.getEvents();
        if (!setEvents.isEmpty()) {
            commonEventService.setViewAndConfirmedRequestsForEvents(new ArrayList<>(setEvents));
        }
        log.debug("Get compilation with ID = {}", compId);
        return mapperDto.mapToCompilationDto(compilation);
    }

    private Compilation getCompilationOrThrowException(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(() -> {
            log.debug("Compilation with ID {} not found", compId);
            return new EntityNotFoundException(String.format("Compilation with id=%s was not found", compId));
        });
    }
}
