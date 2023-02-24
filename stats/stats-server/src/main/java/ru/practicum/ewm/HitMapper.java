package ru.practicum.ewm;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import ru.practicum.ewm.model.Hit;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface HitMapper {
    Hit mapToHit(HitRequestDto dto);

    HitResponseDto mapToDto(Hit hit);

    List<HitResponseDto> mapToListDto(List<Hit> hits);
}
