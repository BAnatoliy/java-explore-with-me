/*
package ru.practicum.ewm.controllers.priv;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dtos.*;
import ru.practicum.ewm.services.UserService;

import java.util.List;


public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    */
/*@GetMapping("/events")
    public List<EventShortDto> getEvents(@PathVariable(value = "userId") Long userId,
                                         @RequestParam(value = "from", defaultValue = "0") Integer from,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return userService.getEvents(userId, from, size);
    }

    @PostMapping("/events")
    public EventFullDto createEvent(@PathVariable(value = "userId") Long userId,
                                   @RequestBody NewEventDto newEventDto) {
        return userService.createEvent(userId, newEventDto);
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getEventById(@PathVariable(value = "userId") Long userId,
                                     @PathVariable(value = "eventId") Long eventId) {
        return userService.getEventsById(userId, eventId);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable(value = "userId") Long userId,
                                              @PathVariable(value = "eventId") Long eventId,
                                              @RequestBody
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                              UpdateEventUserRequest updateEventUserRequest) {
        return userService.updateEvent(userId, eventId, updateEventUserRequest);
    }*//*


    @GetMapping("/events/{eventId}/requests")
    public ParticipationRequestDto getEventRequest(@PathVariable(value = "userId") Long userId,
                                                   @PathVariable(value = "eventId") Long eventId) {
        return userService.getEventRequest(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequest(@PathVariable(value = "userId") Long userId,
                                                        @PathVariable(value = "eventId") Long eventId,
                                                        @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return userService.updateRequest(userId, eventId, eventRequestStatusUpdateRequest);
    }

    @GetMapping("/requests")
    public List<ParticipationRequestDto> getUserRequests(@PathVariable(value = "userId") Long userId) {
        return userService.getUserRequests(userId);
    }

    @PostMapping("/requests")
    public ParticipationRequestDto createUserRequest(@PathVariable(value = "userId") Long userId,
                                                     @RequestParam(value = "eventId") Long eventId) {
        return userService.createUserRequest(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelUserRequest(@PathVariable(value = "userId") Long userId,
                                                     @PathVariable(value = "requestId") Long requestId) {
        return userService.cancelUserRequest(userId, requestId);
    }
}
*/
