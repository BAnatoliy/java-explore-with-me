package ru.practicum.ewm.controllers.admin;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dtos.*;
import ru.practicum.ewm.services.AdminUserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@Validated
public class AdminUserController {
    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(value = "ids") List<Long> ids,
                                  @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                  @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return adminUserService.getUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        return adminUserService.createUser(newUserRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable(value = "userId") Long userId) {
        adminUserService.deleteUser(userId);
    }

    /*@PostMapping("/compilations")
    public CompilationDto createCompilations(@RequestBody NewCompilationDto newCompilationDto) {
        return adminService.createCompilations(newCompilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    public void deleteCompilation(@PathVariable(value = "compId") Long compId) {
        adminService.deleteCompilation(compId);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto updateCompilation(@PathVariable(value = "compId") Long compId,
                                                      @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        return adminService.updateCompilation(compId, updateCompilationRequest);
    }*/

    /*@PostMapping("/categories")
    public CategoryDto createCategory(@RequestBody NewCategoryDto newCategoryDto) {
        return adminService.createCategory(newCategoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    public void deleteCategory(@PathVariable(value = "catId") Long catId) {
        adminService.deleteCategory(catId);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto updateCategory(@PathVariable(value = "catId") Long catId,
                                         @RequestBody NewCategoryDto newCategoryDto) {
        return adminService.updateCategory(catId, newCategoryDto);
    }*/

    /*@GetMapping("/events")
    public List<EventFullDto> getEvents(@RequestParam(value = "users") List<Long> users,
                                        @RequestParam(value = "states") List<EventState> states,
                                        @RequestParam(value = "categories") List<Long> categories,
                                        @RequestParam(value = "rangeStart")
                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                        @RequestParam(value = "rangeEnd")
                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                        @RequestParam(value = "from", defaultValue = "0") Integer from,
                                        @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return adminService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable(value = "eventId") Long eventId) {
        return adminService.updateEvent(eventId);
    }*/
}
