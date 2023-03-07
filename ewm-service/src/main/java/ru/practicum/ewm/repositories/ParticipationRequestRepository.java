package ru.practicum.ewm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.constant.RequestStatus;
import ru.practicum.ewm.models.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByEvent_IdInAndStatusIs(List<Long> eventsIds, RequestStatus requestStatus);

    List<ParticipationRequest> findAllByEvent_IdIsAndStatusIs(Long eventId, RequestStatus requestStatus);

    List<ParticipationRequest> findAllByEvent_IdIs(Long eventId);

    List<ParticipationRequest> findAllByRequester_IdIs(Long userId);
}
