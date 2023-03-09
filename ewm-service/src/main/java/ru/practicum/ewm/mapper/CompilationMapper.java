package ru.practicum.ewm.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import ru.practicum.ewm.dtos.CompilationDto;
import ru.practicum.ewm.dtos.EventShortDto;
import ru.practicum.ewm.models.Compilation;
import ru.practicum.ewm.models.Event;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CompilationMapper {
    CompilationDto mapToCompilationDto(Compilation compilation);

    List<CompilationDto> mapToListCompilationDto(List<Compilation> compilations);

    EventShortDto mapToEventShortDto(Event event);
}
