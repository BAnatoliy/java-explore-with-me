package ru.practicum.ewm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.constant.RequestStatus;
import ru.practicum.ewm.models.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    /**
     * This method sends query to getting a list of participation request
     * from the table "requests" by events and status
     * @param eventsIds IDs of events witch the requests belong
     * @param requestStatus {@link  ru.practicum.ewm.constant.RequestStatus status} of requests
     * @return a list of events or an empty list if events not found
     */
    List<ParticipationRequest> findAllByEvent_IdInAndStatusIs(List<Long> eventsIds, RequestStatus requestStatus);

    /**
     * This method sends query to getting a list of participation request
     * from the table "requests" by event and status
     * @param eventId event`s ID witch the requests belong
     * @param requestStatus {@link  ru.practicum.ewm.constant.RequestStatus status} of requests
     * @return a list of events or an empty list if events not found
     */
    List<ParticipationRequest> findAllByEvent_IdIsAndStatusIs(Long eventId, RequestStatus requestStatus);

    /**
     * This method sends query to getting a list of participation request
     * from the table "requests" by event
     * @param eventId event`s ID witch the requests belong
     * @return a list of events or an empty list if events not found
     */
    List<ParticipationRequest> findAllByEvent_IdIs(Long eventId);

    /**
     * This method sends query to getting a list of participation request
     * from the table "requests" by user
     * @param userId user`s ID who created this request
     * @return a list of events or an empty list if events not found
     */
    List<ParticipationRequest> findAllByRequester_IdIs(Long userId);

    /**
     * This method sends query to getting a Optional<ParticipationRequest> request
     * from the table "requests" by event, user and status
     * @param eventId event`s ID witch the requests belong
     * @param userId user`s ID who created this request
     * @return {@link Optional Optional}
     */
    Optional<ParticipationRequest> findByRequester_IdAndEvent_IdAndStatusIs(Long userId,
                                                                            Long eventId, RequestStatus requestStatus);
}
