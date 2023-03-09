package ru.practicum.ewm.controllers.admin;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dtos.CompilationDto;
import ru.practicum.ewm.dtos.NewCompilationDto;
import ru.practicum.ewm.dtos.UpdateCompilationRequest;
import ru.practicum.ewm.services.AdminCompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@Validated
public class AdminCompilationController {
    private final AdminCompilationService adminCompilationService;

    public AdminCompilationController(AdminCompilationService adminCompilationService) {
        this.adminCompilationService = adminCompilationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilations(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        return adminCompilationService.createCompilations(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable(value = "compId") Long compId) {
        adminCompilationService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable(value = "compId") Long compId,
                                            @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        return adminCompilationService.updateCompilation(compId, updateCompilationRequest);
    }
}
