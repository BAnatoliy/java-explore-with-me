package ru.practicum.ewm.controllers.priv;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dtos.EventFullDto;
import ru.practicum.ewm.dtos.EventShortDto;
import ru.practicum.ewm.dtos.NewEventDto;
import ru.practicum.ewm.dtos.UpdateEventUserRequest;
import ru.practicum.ewm.services.PrivateEventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@Validated
public class PrivateEventController {
    private final PrivateEventService privateEventService;

    public PrivateEventController(PrivateEventService privateEventService) {
        this.privateEventService = privateEventService;
    }

    @GetMapping
    public List<EventShortDto> getEventsByUser(@PathVariable(value = "userId") Long userId,
                                         @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return privateEventService.getEventsByUser(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEventByUser(@PathVariable(value = "userId") Long userId,
                                          @RequestBody @Valid NewEventDto newEventDto) {
        return privateEventService.createEventByUser(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByIdByUser(@PathVariable(value = "userId") Long userId,
                                     @PathVariable(value = "eventId") Long eventId) {
        return privateEventService.getEventsByIdByInitiator(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUser(@PathVariable(value = "userId") Long userId,
                                    @PathVariable(value = "eventId") Long eventId,
                                    @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return privateEventService.updateEventByUser(userId, eventId, updateEventUserRequest);
    }
}
