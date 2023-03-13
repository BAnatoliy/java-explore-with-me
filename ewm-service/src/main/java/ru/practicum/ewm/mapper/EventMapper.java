package ru.practicum.ewm.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.ewm.dtos.*;
import ru.practicum.ewm.models.Category;
import ru.practicum.ewm.models.Comment;
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

    List<CommentDto> mapToListCommentDto(List<Comment> eventComments);

    @Mapping(target = "authorName", source = "author", qualifiedByName = "nameFromAuthor")
    @Mapping(target = "eventId", source = "event", qualifiedByName = "idFromEvent")
    CommentDto mapToCommentDto(Comment comment);

    @Named("nameFromAuthor")
    default String getNameFromAuthor(User author) {
        return author.getName();
    }

    @Named("idFromEvent")
    default Long getIdFromEvent(Event event) {
        return event.getId();
    }
}
