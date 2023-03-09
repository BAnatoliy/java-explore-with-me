package ru.practicum.ewm.services;

import ru.practicum.ewm.dtos.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dtos.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dtos.ParticipationRequestDto;

import java.util.List;

public interface PrivateRequestService {
    List<ParticipationRequestDto> getEventRequest(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequest(Long userId, Long eventId,
                                                 EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<ParticipationRequestDto> getUserRequests(Long userId);

    ParticipationRequestDto createUserRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelUserRequest(Long userId, Long requestId);
}
