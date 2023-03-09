package ru.practicum.ewm.controllers.pub;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dtos.CategoryDto;
import ru.practicum.ewm.services.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryDto> getCategories(@PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                           @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return categoryService.getCategories(from, size);
    }

    @GetMapping("{catId}")
    public CategoryDto getCategoryById(@PathVariable(value = "catId") Long catId) {
        return categoryService.getCategoryById(catId);
    }
}
