package ru.practicum.ewm.services;

import ru.practicum.ewm.dtos.CompilationDto;
import ru.practicum.ewm.dtos.NewCompilationDto;
import ru.practicum.ewm.dtos.UpdateCompilationRequest;

public interface AdminCompilationService {
    CompilationDto createCompilations(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest);
}
