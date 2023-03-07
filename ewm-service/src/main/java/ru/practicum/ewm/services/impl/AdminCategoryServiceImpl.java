package ru.practicum.ewm.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dtos.CategoryDto;
import ru.practicum.ewm.dtos.NewCategoryDto;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.exception.ValidEntityException;
import ru.practicum.ewm.mapper.MapperDto;
import ru.practicum.ewm.models.Category;
import ru.practicum.ewm.repositories.CategoryRepository;
import ru.practicum.ewm.services.AdminCategoryService;

import javax.transaction.Transactional;

@Service
@Slf4j
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final CategoryRepository categoryRepository;
    private final MapperDto mapperDto;

    public AdminCategoryServiceImpl(CategoryRepository categoryRepository, MapperDto mapperDto) {
        this.categoryRepository = categoryRepository;
        this.mapperDto = mapperDto;
    }

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        Category category = mapperDto.mapToCategoryFromNewCategoryDto(newCategoryDto);
        Category savedCategory = categoryRepository.save(category);
        log.debug("Category is saved");
        return mapperDto.mapToCategoryDto(savedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        Category category = getCategoryOrThrowException(catId);
        if (!category.getEvents().isEmpty()) {
            throw new ValidEntityException("The category is not empty");
        }
        categoryRepository.deleteById(catId);
        log.debug("Category with ID = {} is deleted", catId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, NewCategoryDto newCategoryDto) {
        getCategoryOrThrowException(catId);
        Category category = mapperDto.mapToCategoryFromNewCategoryDto(newCategoryDto);
        category.setId(catId);
        category.setName(newCategoryDto.getName());
        Category savedCategory = categoryRepository.save(category);
        log.debug("Category with ID = {} is updated", catId);
        return mapperDto.mapToCategoryDto(savedCategory);
    }

    private Category getCategoryOrThrowException(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Category with id=%s was not found", catId)));
    }
}
