package ru.practicum.ewm.controllers.priv;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dtos.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dtos.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dtos.ParticipationRequestDto;
import ru.practicum.ewm.services.PrivateRequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
@Validated
public class PrivateRequestController {
    private final PrivateRequestService privateRequestService;

    public PrivateRequestController(PrivateRequestService privateRequestService) {
        this.privateRequestService = privateRequestService;
    }

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequest(@PathVariable(value = "userId") Long userId,
                                                         @PathVariable(value = "eventId") Long eventId) {
        return privateRequestService.getEventRequest(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequest(@PathVariable(value = "userId") Long userId,
                                                        @PathVariable(value = "eventId") Long eventId,
                                                        @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return privateRequestService.updateRequest(userId, eventId, eventRequestStatusUpdateRequest);
    }

    @GetMapping("/requests")
    public List<ParticipationRequestDto> getUserRequests(@PathVariable(value = "userId") Long userId) {
        return privateRequestService.getUserRequests(userId);
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createUserRequest(@PathVariable(value = "userId") Long userId,
                                                     @RequestParam(value = "eventId") Long eventId) {
        return privateRequestService.createUserRequest(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelUserRequest(@PathVariable(value = "userId") Long userId,
                                                     @PathVariable(value = "requestId") Long requestId) {
        return privateRequestService.cancelUserRequest(userId, requestId);
    }
}
