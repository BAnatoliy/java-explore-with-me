package ru.practicum.ewm.controllers.pub;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.constant.SortValue;
import ru.practicum.ewm.dtos.EventFullDto;
import ru.practicum.ewm.dtos.EventShortDto;
import ru.practicum.ewm.services.PublicEventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {
    private final PublicEventService publicEventService;

    public EventController(PublicEventService publicEventService) {
        this.publicEventService = publicEventService;
    }

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(value = "text", required = false) @NotEmpty String text,
                                         @RequestParam(value = "categories", required = false) List<Long> categories,
                                         @RequestParam(value = "paid", required = false) Boolean paid,
                                         @RequestParam(value = "rangeStart", required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                         LocalDateTime rangeStart,
                                         @RequestParam(value = "rangeEnd", required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                         LocalDateTime rangeEnd,
                                         @RequestParam(value = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(value = "sort", required = false) SortValue sort,
                                         @RequestParam(value = "from", defaultValue = "0") Integer from,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                         HttpServletRequest request) {
        return publicEventService.getEvents(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable(value = "id") Long eventId, HttpServletRequest request) {
        return publicEventService.getEventById(eventId, request);
    }
}
