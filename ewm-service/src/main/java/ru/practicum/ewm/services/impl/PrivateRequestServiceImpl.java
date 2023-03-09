package ru.practicum.ewm.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.constant.EventState;
import ru.practicum.ewm.constant.RequestStatus;
import ru.practicum.ewm.constant.RequestStatusToUpdate;
import ru.practicum.ewm.dtos.EventFullDto;
import ru.practicum.ewm.dtos.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dtos.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dtos.ParticipationRequestDto;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.exception.ValidEntityException;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.models.Event;
import ru.practicum.ewm.models.ParticipationRequest;
import ru.practicum.ewm.models.User;
import ru.practicum.ewm.repositories.ParticipationRequestRepository;
import ru.practicum.ewm.services.CommonEventService;
import ru.practicum.ewm.services.PrivateEventService;
import ru.practicum.ewm.services.PrivateRequestService;
import ru.practicum.ewm.services.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PrivateRequestServiceImpl implements PrivateRequestService {
    private final ParticipationRequestRepository participationRequestRepository;
    private final UserService userService;
    private final CommonEventService commonEventService;
    private final PrivateEventService privateEventService;
    private final RequestMapper requestMapper;

    public PrivateRequestServiceImpl(ParticipationRequestRepository participationRequestRepository,
                                     UserService userService, CommonEventService commonEventService,
                                     PrivateEventService privateEventService, RequestMapper requestMapper) {
        this.participationRequestRepository = participationRequestRepository;
        this.userService = userService;
        this.commonEventService = commonEventService;
        this.privateEventService = privateEventService;
        this.requestMapper = requestMapper;
    }

    @Override
    public List<ParticipationRequestDto> getEventRequest(Long userId, Long eventId) {
        privateEventService.getEventsByIdByInitiator(userId, eventId);
        List<ParticipationRequest> eventRequests = participationRequestRepository.findAllByEvent_IdIs(eventId);
        log.debug("Get the event`s request list by event ID = {} and user ID = {}", eventId, userId);
        return requestMapper.mapToListRequestsDto(eventRequests);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequest(Long userId, Long eventId,
                                                        EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        EventFullDto event = privateEventService.getEventsByIdByInitiator(userId, eventId);
        List<Long> requestIds = eventRequestStatusUpdateRequest.getRequestIds();
        List<ParticipationRequest> requestsToUpdate = participationRequestRepository.findAllById(requestIds);
        setNewStatusToRequest(eventRequestStatusUpdateRequest, event, requestsToUpdate);

        List<ParticipationRequest> updatedRequests = participationRequestRepository.saveAll(requestsToUpdate);
        List<ParticipationRequestDto> requestDtos = requestMapper.mapToListRequestsDto(updatedRequests);
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();

        List<ParticipationRequestDto> confirmedRequest = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequest = new ArrayList<>();

        requestDtos.forEach((request) -> {
            if (request.getStatus() == RequestStatus.CONFIRMED) {
                confirmedRequest.add(request);
            } else if (request.getStatus() == RequestStatus.REJECTED) {
                rejectedRequest.add(request);
            }
        });

        result.setConfirmedRequests(confirmedRequest);
        result.setRejectedRequests(rejectedRequest);
        log.debug("Requests status was updated");
        return result;
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        userService.getUserOrThrowException(userId);
        List<ParticipationRequest> requests = participationRequestRepository.findAllByRequester_IdIs(userId);
        log.debug("Get the user`s requests list");
        return requestMapper.mapToListRequestsDto(requests);
    }

    @Override
    @Transactional
    public ParticipationRequestDto createUserRequest(Long userId, Long eventId) {
        Event event = commonEventService.getEventOrThrowException(eventId);
        commonEventService.setViewAndConfirmedRequestRequestsForTheEvent(event);
        validRequest(userId, event);
        User requester = userService.getUserOrThrowException(userId);
        /*Optional<ParticipationRequest> requestOptional = participationRequestRepository.findByRequester_IdIsAndEvent_IdIs(userId, eventId);
        if (requestOptional.isPresent()) {
            throw new ValidEntityException();
        }*/
        ParticipationRequest request = new ParticipationRequest();
        request.setCreated(LocalDateTime.now());
        request.setRequester(requester);
        request.setEvent(event);

        if (event.getRequestModeration()) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        ParticipationRequest savedRequest = participationRequestRepository.save(request);
        log.debug("Request was created");
        return requestMapper.mapToRequestDto(savedRequest);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelUserRequest(Long userId, Long requestId) {
        userService.getUserOrThrowException(userId);
        ParticipationRequest request = getRequestOrThrowException(requestId);
        if (!request.getRequester().getId().equals(userId)) {
            throw new ValidEntityException(String.format("User with ID = %s is not requester the request", userId));
        }
        request.setStatus(RequestStatus.CANCELED);
        ParticipationRequest updatedRequest = participationRequestRepository.save(request);
        log.debug("Request with ID = {} was canceled", requestId);
        return requestMapper.mapToRequestDto(updatedRequest);
    }

    private ParticipationRequest getRequestOrThrowException(Long requestId) {
        return participationRequestRepository.findById(requestId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Request with id=%s was not found", requestId))
        );
    }

    private void setNewStatusToRequest(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
                                       EventFullDto event, List<ParticipationRequest> requestsToUpdate) {
        long eventConfirmedRequest = event.getConfirmedRequests();
        int limitRequest = event.getParticipantLimit();

        if (limitRequest == 0 || !event.getRequestModeration()) {
            requestsToUpdate.forEach((request) -> request.setStatus(RequestStatus.CONFIRMED));
        } else {
            if (eventConfirmedRequest == limitRequest) {
                throw new ValidEntityException("The participant limit has been reached");
            }

            if (eventRequestStatusUpdateRequest.getStatus() == RequestStatusToUpdate.REJECTED) {
                requestsToUpdate.forEach((request) -> request.setStatus(RequestStatus.REJECTED));
            } else {
                for (ParticipationRequest request : requestsToUpdate) {
                    if (request.getStatus() != RequestStatus.PENDING) {
                        throw new ValidEntityException("Request status must be PENDING");
                    }

                    if (eventConfirmedRequest <= limitRequest) {
                        request.setStatus(RequestStatus.CONFIRMED);
                        eventConfirmedRequest++;
                    } else {
                        request.setStatus(RequestStatus.REJECTED);
                    }
                }
            }
        }
    }

    private void validRequest(Long userId, Event event) {
        if (event.getInitiator().getId().equals(userId)) {
            throw new ValidEntityException("Event`s initiator can not be a requester");
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new ValidEntityException("Event must be published");
        }
        if (event.getConfirmedRequests() == (int) event.getParticipantLimit()) {
            throw new ValidEntityException("The participant limit has been reached");
        }
    }
}
