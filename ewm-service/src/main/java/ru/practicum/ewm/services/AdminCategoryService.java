package ru.practicum.ewm.services;

import ru.practicum.ewm.dtos.CategoryDto;
import ru.practicum.ewm.dtos.NewCategoryDto;

public interface AdminCategoryService {
    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, NewCategoryDto newCategoryDto);
}
