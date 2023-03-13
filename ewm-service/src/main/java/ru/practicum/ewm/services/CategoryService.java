package ru.practicum.ewm.services;

import ru.practicum.ewm.dtos.CategoryDto;
import ru.practicum.ewm.models.Category;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long catId);

    Category findCategoryById(Long catId);
}
