package ru.practicum.ewm.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.ewm.dtos.ParticipationRequestDto;
import ru.practicum.ewm.models.Event;
import ru.practicum.ewm.models.ParticipationRequest;
import ru.practicum.ewm.models.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RequestMapper {
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
}
