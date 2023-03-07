package ru.practicum.ewm.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dtos.CategoryDto;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.mapper.MapperDto;
import ru.practicum.ewm.models.Category;
import ru.practicum.ewm.repositories.CategoryRepository;
import ru.practicum.ewm.services.CategoryService;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final MapperDto mapperDto;

    public CategoryServiceImpl(CategoryRepository categoryRepository, MapperDto mapperDto) {
        this.categoryRepository = categoryRepository;
        this.mapperDto = mapperDto;
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        List<Category> categoryList = categoryRepository.findAllByFromSize(from, size);
        log.debug("Get categories list with parameters from = {}, size = {}", from, size);
        return mapperDto.mapToListCategoryDto(categoryList);
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Category with id=%s was not found", catId)));
        log.debug("Category with ID = {} was found", catId);
        return mapperDto.mapToCategoryDto(category);
    }
}
