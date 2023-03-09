package ru.practicum.ewm.controllers.admin;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.constant.EventState;
import ru.practicum.ewm.dtos.EventFullDto;
import ru.practicum.ewm.dtos.UpdateEventAdminRequest;
import ru.practicum.ewm.services.AdminEventService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
public class AdminEventController {
    private final AdminEventService adminEventService;

    public AdminEventController(AdminEventService adminEventService) {
        this.adminEventService = adminEventService;
    }

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(value = "users", required = false) List<Long> users,
                                        @RequestParam(value = "states", required = false) List<EventState> states,
                                        @RequestParam(value = "categories", required = false) List<Long> categories,
                                        @RequestParam(value = "rangeStart", required = false)
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                            LocalDateTime rangeStart,
                                        @RequestParam(value = "rangeEnd", required = false)
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                            LocalDateTime rangeEnd,
                                        @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                        @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return adminEventService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable(value = "eventId") Long eventId,
                                    @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        return adminEventService.updateEvent(eventId, updateEventAdminRequest);
    }
}
