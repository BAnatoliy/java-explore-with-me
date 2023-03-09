package ru.practicum.ewm.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dtos.*;
import ru.practicum.ewm.models.Category;
import ru.practicum.ewm.models.Event;
import ru.practicum.ewm.models.User;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface EventMapper {
    EventFullDto mapToEventFullDto(Event event);

    List<EventShortDto> mapToListEventShortDto(List<Event> events);

    @Mapping(target = "category", ignore = true)
    Event mapToEvent(NewEventDto newEventDto);

    List<EventFullDto> mapToListEventFullDto(List<Event> events);

    UserShortDto mapToUserShortDto(User user);

    CategoryDto mapToCategoryDto(Category category);
}
