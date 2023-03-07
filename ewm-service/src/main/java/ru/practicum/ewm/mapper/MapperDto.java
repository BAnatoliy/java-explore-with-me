package ru.practicum.ewm.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.ewm.dtos.*;
import ru.practicum.ewm.models.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MapperDto {

    CompilationDto mapToCompilationDto(Compilation compilation);

    List<CompilationDto> mapToListCompilationDto(List<Compilation> compilations);

    Compilation mapToCompilation(CompilationDto compilationDto);

    EventShortDto mapToEventShortDto(Event event);

    EventFullDto mapToEventFullDto(Event event);

    CategoryDto mapToCategoryDto(Category category);

    List<CategoryDto> mapToListCategoryDto(List<Category> categories);

    UserShortDto mapToUserShortDto(User user);

    UserDto mapToUserDto(User user);

    List<EventShortDto> mapToListEventShortDto(List<Event> events);

    @Mapping(target = "category", ignore = true)
    Event mapToEvent(NewEventDto newEventDto);

    List<UserDto> mapToUserDtoList(List<User> users);

    User mapToUser(NewUserRequest newUserRequest);

    Category mapToCategoryFromNewCategoryDto(NewCategoryDto newCategoryDto);

    Category mapToCategoryFromCategoryDto(CategoryDto categoryDto);

    List<ParticipationRequestDto> mapToListRequestsDto(List<ParticipationRequest> eventRequests);

    @Mapping(target = "event", source = "event", qualifiedByName = "getIdFromEvent")
    @Mapping(target = "requester", source = "requester", qualifiedByName = "getIdFromUser")
    @Mapping(target = "created", source = "created", qualifiedByName = "getStringCreated")
    ParticipationRequestDto mapToRequestDto(ParticipationRequest request);

    @Named("getIdFromEvent")
    default Long getIdFromEvent(Event event) {
        return event.getId();
    }

    @Named("getIdFromUser")
    default Long getIdFromUser(User user) {
        return user.getId();
    }

    @Named("getStringCreated")
    default String getStringCreated(LocalDateTime created) {
        return created.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    List<EventFullDto> mapToListEventFullDto(List<Event> events);
}
