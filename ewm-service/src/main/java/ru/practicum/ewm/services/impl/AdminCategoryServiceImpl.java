package ru.practicum.ewm.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dtos.CategoryDto;
import ru.practicum.ewm.dtos.NewCategoryDto;
import ru.practicum.ewm.exception.ValidEntityException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.models.Category;
import ru.practicum.ewm.repositories.CategoryRepository;
import ru.practicum.ewm.services.AdminCategoryService;
import ru.practicum.ewm.services.CategoryService;

import javax.transaction.Transactional;

@Service
@Slf4j
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public AdminCategoryServiceImpl(CategoryService categoryService, CategoryRepository categoryRepository,
                                    CategoryMapper categoryMapper) {
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    /**
     * This method saves the category`s data obtained from the NewCategoryDto
     * in the database.
     * @param newCategoryDto {@link ru.practicum.ewm.dtos.NewCategoryDto dto} which category is created from
     * @return {@link ru.practicum.ewm.dtos.CategoryDto CategoryDto} gotten from
     * {@link ru.practicum.ewm.models.Category Category}
     */
    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        Category category = categoryMapper.mapToCategoryFromNewCategoryDto(newCategoryDto);
        Category savedCategory = categoryRepository.save(category);
        log.debug("Category is saved");
        return categoryMapper.mapToCategoryDto(savedCategory);
    }

    /**
     * This method deletes the category by ID from the database
     * @param catId ID of category which will be deleted
     */
    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        Category category = categoryService.findCategoryById(catId); //поиск категории по ID, если не найдена - исключение
        if (!category.getEvents().isEmpty()) { //если в категории есть события ее невозможно удалить
            throw new ValidEntityException("The category is not empty");
        }
        categoryRepository.deleteById(catId);
        log.debug("Category with ID = {} is deleted", catId);
    }

    /**
     * This method updates the category`s data obtained from the NewCategoryDto
     * in the database.
     * @param newCategoryDto {@link ru.practicum.ewm.dtos.NewCategoryDto dto} which the category is updated from
     * @param catId ID of category which will be updated
     * @return {@link ru.practicum.ewm.dtos.CategoryDto CategoryDto} gotten from
     * {@link ru.practicum.ewm.models.Category Category}
     */
    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, NewCategoryDto newCategoryDto) {
        categoryService.findCategoryById(catId); //поиск категории по ID, если не найдена - исключение
        Category category = categoryMapper.mapToCategoryFromNewCategoryDto(newCategoryDto);
        category.setId(catId);
        category.setName(newCategoryDto.getName());
        Category savedCategory = categoryRepository.save(category);
        log.debug("Category with ID = {} is updated", catId);
        return categoryMapper.mapToCategoryDto(savedCategory);
    }
}
